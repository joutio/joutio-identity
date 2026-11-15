package com.joutio.identity.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

/**
 * Modèle domaine représentant un utilisateur tel que vu par le domaine.
 * N'importe AUCUN type JPA, Spring, ou framework.
 * Immuable par convention (record).
 *
 * <p>Aucun mot de passe : l'authentification est passwordless (OTP email) ou sociale.</p>
 */
public record User(
        String userId,
        String email,
        Set<SocialProvider> linkedSocialAccounts,
        Instant createdAt,
        AccountStatus status,
        UserSuspension suspension,
        UserPremium premium,
        /** Privée : exposée au seul joueur concerné. Nulle tant qu'elle n'est pas renseignée. */
        LocalDate birthDate,
        /** Version des CGU acceptée (2026-11-15) ; {@code null} = jamais acceptées explicitement. */
        String termsVersion,
        /** Date de cette acceptation. */
        Instant termsAcceptedAt
) {

    public User {
        status = status == null ? AccountStatus.ACTIVE : status;
    }

    /** Constructeur de compatibilité pour les comptes jamais suspendus. */
    public User(String userId, String email, Set<SocialProvider> linkedSocialAccounts, Instant createdAt) {
        this(userId, email, linkedSocialAccounts, createdAt, AccountStatus.ACTIVE, null, null, null);
    }

    /** Constructeur de compatibilité pour les comptes sans droit Premium. */
    public User(String userId,
                String email,
                Set<SocialProvider> linkedSocialAccounts,
                Instant createdAt,
                AccountStatus status,
                UserSuspension suspension) {
        this(userId, email, linkedSocialAccounts, createdAt, status, suspension, null, null);
    }

    /** Constructeur de compatibilité pour les comptes sans date de naissance. */
    public User(String userId,
                String email,
                Set<SocialProvider> linkedSocialAccounts,
                Instant createdAt,
                AccountStatus status,
                UserSuspension suspension,
                UserPremium premium) {
        this(userId, email, linkedSocialAccounts, createdAt, status, suspension, premium, null);
    }

    /** Constructeur de compatibilité pour les comptes sans acceptation des CGU enregistrée. */
    public User(String userId,
                String email,
                Set<SocialProvider> linkedSocialAccounts,
                Instant createdAt,
                AccountStatus status,
                UserSuspension suspension,
                UserPremium premium,
                LocalDate birthDate) {
        this(userId, email, linkedSocialAccounts, createdAt, status, suspension, premium, birthDate, null, null);
    }

    public User withLinkedSocialAccounts(Set<SocialProvider> accounts) {
        return new User(userId, email, accounts, createdAt, status, suspension, premium, birthDate, termsVersion, termsAcceptedAt);
    }

    public User withStatus(AccountStatus nextStatus, UserSuspension nextSuspension) {
        return new User(userId, email, linkedSocialAccounts, createdAt, nextStatus, nextSuspension, premium, birthDate, termsVersion, termsAcceptedAt);
    }

    public User withPremium(UserPremium nextPremium) {
        return new User(userId, email, linkedSocialAccounts, createdAt, status, suspension, nextPremium, birthDate, termsVersion, termsAcceptedAt);
    }

    public User withBirthDate(LocalDate nextBirthDate) {
        return new User(userId, email, linkedSocialAccounts, createdAt, status, suspension, premium, nextBirthDate, termsVersion, termsAcceptedAt);
    }

    public User withTermsAccepted(String version, Instant acceptedAt) {
        return new User(userId, email, linkedSocialAccounts, createdAt, status, suspension, premium, birthDate, version, acceptedAt);
    }

    /** Droit Premium effectif : accordé, non échu, compte ni suspendu ni supprimé. */
    public boolean premiumActive(Instant now) {
        return PremiumRules.isEffective(status, suspension == null ? null : suspension.suspendedUntil(), premium, now);
    }

    public boolean suspended(Instant now) {
        return AccountStatusRules.isSuspended(status, suspension == null ? null : suspension.suspendedUntil(), now);
    }
}
