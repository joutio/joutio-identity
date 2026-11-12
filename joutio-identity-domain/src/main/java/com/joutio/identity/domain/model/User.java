package com.joutio.identity.domain.model;

import java.time.Instant;
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
        UserPremium premium
) {

    public User {
        status = status == null ? AccountStatus.ACTIVE : status;
    }

    /** Constructeur de compatibilité pour les comptes jamais suspendus. */
    public User(String userId, String email, Set<SocialProvider> linkedSocialAccounts, Instant createdAt) {
        this(userId, email, linkedSocialAccounts, createdAt, AccountStatus.ACTIVE, null, null);
    }

    /** Constructeur de compatibilité pour les comptes sans droit Premium. */
    public User(String userId,
                String email,
                Set<SocialProvider> linkedSocialAccounts,
                Instant createdAt,
                AccountStatus status,
                UserSuspension suspension) {
        this(userId, email, linkedSocialAccounts, createdAt, status, suspension, null);
    }

    /** Droit Premium effectif : accordé, non échu, compte ni suspendu ni supprimé. */
    public boolean premiumActive(Instant now) {
        return PremiumRules.isEffective(status, suspension == null ? null : suspension.suspendedUntil(), premium, now);
    }

    public boolean suspended(Instant now) {
        return AccountStatusRules.isSuspended(status, suspension == null ? null : suspension.suspendedUntil(), now);
    }
}
