package com.joutio.identity.domain.model;

import java.time.Instant;

/**
 * Vue légère du statut d'un compte, utilisée par les filtres, les badges publics et la lecture
 * du droit Premium par le BFF, pour le joueur courant comme sur les vues joueur (fraîche : purgée sur les
 * événements identity).
 */
public record UserAccountStatus(
        String userId,
        AccountStatus status,
        Instant suspendedUntil,
        UserPremium premium,
        /** {@code ROLE_ADMIN} : Premium par défaut. */
        boolean admin
) {

    /** Droit Premium accordé et effectif (hors {@code ROLE_ADMIN}). */
    public boolean premiumActive(Instant now) {
        return PremiumRules.isEffective(status, suspendedUntil, premium, now);
    }

    /** Premium affiché et accordé : droit effectif ou admin, jamais pour un compte suspendu. */
    public boolean premium(Instant now) {
        return !suspended(now) && (premiumActive(now) || admin);
    }

    public boolean suspended(Instant now) {
        return AccountStatusRules.isSuspended(status, suspendedUntil, now);
    }

    public boolean deleted() {
        return status == AccountStatus.DELETED;
    }
}
