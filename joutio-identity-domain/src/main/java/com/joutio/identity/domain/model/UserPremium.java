package com.joutio.identity.domain.model;

import java.time.Instant;

/**
 * Droit Premium porté par un compte. {@code premiumUntil} nul = sans échéance.
 *
 * <p>Un droit échu reste stocké (historique) mais n'est plus actif ; un compte suspendu n'exerce
 * jamais Premium, ce que vérifient les lecteurs (voir {@link PremiumRules}).</p>
 */
public record UserPremium(
        PremiumSource source,
        Instant premiumUntil,
        String grantedBy,
        Instant grantedAt
) {

    public boolean active(Instant now) {
        return PremiumRules.isActive(this, now);
    }
}
