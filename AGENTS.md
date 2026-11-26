# AGENTS.md — joutio-identity

**IdP** de la plateforme et source de vérité du **compte** : serveur d'autorisation OIDC, connexion
sans mot de passe (OTP par email) ou sociale (Google, Facebook), date de naissance, CGU, statut
de compte et suspension, signalements de joueurs, droit **Premium**. Package `com.joutio.identity`,
base `joutio_identity`, port local 8085.

Lire : `../../AGENTS.md`, [`docs/backend/`](../../docs/backend/hexagonal.md), le
[contexte compte](../../docs/architecture.md#identité-compte-et-droits). Aucun pseudonyme ici : le
profil vit dans `joutio-profile`.

## Périmètre

- **Seul service à garder du REST** (OIDC et API d'authentification). Sans UI hébergée, sauf la
  page `/sign-in` des outils techniques.
- Agrégats :
  - `UserAggregate` : inscription, providers liés, connexions, date de naissance, CGU, Premium,
    suspension, suppression ;
  - `UserReportThreadAggregate` : un fil par paire signalé × signalant, un seul `OPEN` à la fois.
- Aucune dépendance sortante vers un autre service.

## Authentification

```text
Web     POST /api/auth/web/request-login-code {email} → 202 (anti-énumération)
        POST /api/auth/web/login-with-code {email, code} → 200 {userId,email} + cookie AUTH_TX
        GET  /oauth2/authorize (session trouvée) → code ; POST /oauth2/token + code_verifier → jetons
        GET  /api/auth/web/start-google-login | start-facebook-login → 302 ; POST /api/auth/web/logout → 204
Mobile  POST /api/auth/mobile/request-login-code | login-with-code → {userId,email,accessToken,refreshToken,tokenType,expiresIn}
        POST /api/auth/mobile/login-with-google {idToken} | login-with-facebook {idToken,nonce} (iOS) ou {accessToken} (Android)
        POST /oauth2/token (refresh_token, rotation) ; POST /api/auth/mobile/logout {refreshToken} → 204
OIDC    /oauth2/{authorize,token,jwks,revoke,introspect}, /.well-known/openid-configuration, /connect/logout
```

- La première connexion **crée le compte**. L'API web ne renvoie jamais de JWT ; le mobile reçoit
  les jetons directement (grant `urn:joutio:native`, sessions dans `oauth2_authorization`).
- Avant d'émettre la session d'un compte tout juste créé (OTP, Google, Facebook), identity attend
  que `user-projection` l'ait écrit dans `user_entry` (`RegisteredUserWaiter`, 5 s max) : sinon le
  BFF rejetterait la première requête comme celle d'un compte supprimé.
- Une API d'auth **orientée action** (`request-login-code`, `login-with-code`…), assumée pour la
  lisibilité.
- Sans session sur `/oauth2/authorize` :
  - clients joueurs (`web`, `mobile`) → redirection vers la connexion de l'app
    (`app.security.login-page-uri`) ;
  - outils (`app.security.tool-clients` : `console`, `grafana`, `argocd`, `swagger`) → page
    `/sign-in` (OTP ou Google), puis `/sign-in/continue`.
- Facebook sans email → refus, le client propose l'OTP.
- Erreurs `ProblemDetail` :
  - `401` code ou jeton social invalide ;
  - `403` compte suspendu (`suspendedUntil`) ;
  - `422` email Facebook manquant ;
  - `503` fournisseur social indisponible.
- **JWT** (`JwtTokenCustomizer`) : `sub`, `email`, `user_email`, `user_id`, `roles`, `aud`
  (optionnel), **sans claim `name`**. Rôles (`Roles.forUser`) :
  - `ROLE_USER` pour tous ;
  - `ROLE_ADMIN` pour le compte système et `app.authorization-server.admin-emails`.

## Règles métier

- **Compte système** (`JoutioConstants.SYSTEM_USER_ID`) : unique, admin et bot, sans credential,
  non connectable, seedé par `DataSeeder`. Jamais suspendable ni Premium.
- **Date de naissance** (`SetBirthDateCommand`) : 15 ans minimum (`urn:joutio:identity:underage`),
  privée.
- **CGU** (`AcceptTermsCommand`) : idempotent, version et date projetées. La comparaison avec la
  version en vigueur est faite par le BFF.
- **Suspension** (`SuspendUserCommand`, `ReinstateUserCommand`) :
  - décision exclusivement admin, double suspension refusée, réintégration idempotente ;
  - une échéance passée vaut actif à la lecture (`AccountStatusRules`), avant même que
    `SuspensionExpirySweeper` réintègre ;
  - à la suspension : sessions `AUTH_TX` et autorisations OAuth2 supprimées, OTP, social et refresh
    refusés (`invalid_grant account_suspended`), email envoyé (la note reste interne).
- **Premium** (`GrantPremiumCommand`, `RevokePremiumCommand`) :
  - `premiumUntil` nul = sans échéance ;
  - `PremiumSource` ∈ `MANUAL|APP_STORE|PLAY|WEB` (seul `MANUAL` est émis aujourd'hui) ;
  - Grant idempotent sur un droit identique, Revoke idempotent ;
  - `GetUserEntitlementsQuery` → `premium` = droit actif, faux si le compte est suspendu (`PremiumRules`) ;
    `ROLE_ADMIN` ne donne pas le Premium (2026-11-20 : concepts dissociés).
- **Suppression de compte** (`DeleteAccountCommand`, code OTP dédié) → `UserAccountDeletedEvent`,
  nettoyage des sessions et effacement de l'event store.
- **Signalements** (`ReportUserCommand`, `ResolveUserReportCommand`) : snapshot du pseudonyme et
  de l'avatar, contexte `PROFILE|GAME`. Pas d'auto-suspension.

## Contrats (`joutio-identity-domain`)

- Événements :
  - `UserRegisteredEvent` → profile (saga de création) ;
  - `UserLoggedInEvent` ;
  - `SocialProviderLinkedEvent` ;
  - `UserBirthDateSetEvent` ;
  - `UserTermsAcceptedEvent` ;
  - `UserPremiumGranted/RevokedEvent` → BFF (purge de cache) ;
  - `UserSuspendedEvent` → notification (inbox) et BFF ;
  - `UserReinstatedEvent` ;
  - `UserReportedEvent` ;
  - `UserReportReviewedEvent` ;
  - `UserAccountDeletedEvent` → tous les services concernés.
- Requêtes `UserQuery.*` :
  - `GetUserQuery`, `FindUserQuery`, `FindUserByEmailQuery`, `GetUsersByIdsQuery` ;
  - `GetUserAccountStatus(es)Query`, `GetUserEntitlementsQuery` ;
  - `UserSearchQuery`, avec les filtres admin `status` (suspension effective) et `reported` ;
  - `WatchUsersQuery` : surveillance pour les caches du BFF, `UserUpdate` émis par `UserProjection` à chaque écriture
    du compte (version `live_version`, V2 ; [axon](../../docs/backend/axon.md#vues-surveillées-caches-du-bff)) ;
  - `UserReportQuery.*`.

## Multi-instance

- Sessions Spring Session JDBC (cookie `AUTH_TX`).
- Codes OTP en base : hash BCrypt, TTL, tentatives, usage unique.
- Codes, jetons et consentements OAuth2 en JDBC.
- Clients OAuth2 déclarés par `spring.security.oauth2.authorizationserver.client.*`, en mémoire :
  identiques sur chaque pod, et un rollout suffit à les mettre à jour.
- Clé de signature partagée par `JOUTIO_IDENTITY_JWK` (`app.jwk.jwk-set`) ; clé éphémère en profil
  `local` seulement.

## Configuration

| Propriété | Rôle |
|---|---|
| `app.authorization-server.issuer`, `.audience`, `.admin-emails` | issuer public, `aud`, admins (`JOUTIO_ADMIN_EMAILS`) |
| `app.mail.api-key`, `.from`, `.base-url` | envoi des OTP par Resend |
| `app.auth.dev-fixed-code` | code OTP fixe, profil `local` et E2E seulement |
| `app.auth.load-test.email-domain`, `.code` | joueurs synthétiques des tests de charge (vide = désactivé ; piloté par `stress-test/bin/campaign.sh`) |
| `app.auth.mobile.client-id`, `.google-client-id`, `.facebook-app-id` | client public mobile, audiences Google et Facebook |
| `app.security.login-page-uri`, `.tool-clients`, `.oauth2.success-redirect-uri`, `.cors.allowed-origins`, `.session.cookie-*` | redirections, CORS, cookie |
| `spring.security.oauth2.authorizationserver.client.{web,mobile}.token` | access `PT1H`, refresh `P365D`, rotation |
| `joutio.account-suspension.sweep-interval-ms` | balayeur des suspensions échues (60 s) |

## Commandes

```bash
mvn -q -pl joutio-identity-domain test
mvn -pl joutio-identity-infrastructure test
```

## Pièges

- Le seeder OIDC ne met pas à jour un client existant : supprimer sa ligne
  `oauth2_registered_client`, puis redémarrer.
- Domaine d'envoi Resend non vérifié : l'OTP échoue en `403`, alors que l'API répond `202`.
  Vérifier les logs (`Failed to send login code`).
- Les filtres admin de `UserSearchQuery` doivent être déployés avant le BFF qui les émet.
