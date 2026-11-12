package com.joutio.identity.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PremiumRulesTest {

    private static final Instant NOW = Instant.parse("2026-01-15T00:00:00Z");

    @Test
    void noGrant_isInactive() {
        assertFalse(PremiumRules.isActive(null, NOW));
    }

    @Test
    void grantWithoutEnd_isActive() {
        assertTrue(PremiumRules.isActive(grant(null), NOW));
    }

    @Test
    void futureEnd_isActive_pastEnd_isNot() {
        assertTrue(PremiumRules.isActive(grant(NOW.plusSeconds(1)), NOW));
        assertFalse(PremiumRules.isActive(grant(NOW), NOW));
    }

    @Test
    void suspendedOrDeletedAccount_isNeverEffective() {
        assertFalse(PremiumRules.isEffective(AccountStatus.SUSPENDED, null, grant(null), NOW));
        assertFalse(PremiumRules.isEffective(AccountStatus.DELETED, null, grant(null), NOW));
        assertTrue(PremiumRules.isEffective(AccountStatus.ACTIVE, null, grant(null), NOW));
    }

    @Test
    void expiredSuspension_doesNotBlockPremium() {
        assertTrue(PremiumRules.isEffective(AccountStatus.SUSPENDED, NOW.minusSeconds(1), grant(null), NOW));
    }

    private static UserPremium grant(Instant until) {
        return new UserPremium(PremiumSource.MANUAL, until, "admin-1", NOW.minusSeconds(3600));
    }
}
