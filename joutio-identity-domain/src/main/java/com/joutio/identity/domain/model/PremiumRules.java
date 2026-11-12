package com.joutio.identity.domain.model;

import java.time.Instant;
import java.util.Objects;

/** Règles pures du droit Premium. */
public interface PremiumRules {

    /** Actif si accordé et sans échéance ou d'échéance future. */
    static boolean isActive(UserPremium premium, Instant now) {
        if (premium == null) {
            return false;
        }
        return premium.premiumUntil() == null
                || premium.premiumUntil().isAfter(Objects.requireNonNull(now, "now"));
    }

    /**
     * Droit effectif : un compte suspendu ou supprimé n'exerce aucun droit Premium, même accordé.
     */
    static boolean isEffective(AccountStatus status, Instant suspendedUntil, UserPremium premium, Instant now) {
        if (status == AccountStatus.DELETED || AccountStatusRules.isSuspended(status, suspendedUntil, now)) {
            return false;
        }
        return isActive(premium, now);
    }
}
