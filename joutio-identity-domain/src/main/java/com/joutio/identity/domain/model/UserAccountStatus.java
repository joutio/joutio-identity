package com.joutio.identity.domain.model;

import java.time.Instant;

/**
 * Vue légère du statut d'un compte, utilisée par les filtres, les badges publics et la lecture
 * du droit Premium par le BFF (fraîche : purgée sur les événements identity).
 */
public record UserAccountStatus(
        String userId,
        AccountStatus status,
        Instant suspendedUntil,
        UserPremium premium
) {

    /** Constructeur de compatibilité pour les comptes sans droit Premium. */
    public UserAccountStatus(String userId, AccountStatus status, Instant suspendedUntil) {
        this(userId, status, suspendedUntil, null);
    }

    /** Droit Premium effectif (hors {@code ROLE_ADMIN}, lu ailleurs). */
    public boolean premiumActive(Instant now) {
        return PremiumRules.isEffective(status, suspendedUntil, premium, now);
    }

    public boolean suspended(Instant now) {
        return AccountStatusRules.isSuspended(status, suspendedUntil, now);
    }

    public boolean deleted() {
        return status == AccountStatus.DELETED;
    }
}
