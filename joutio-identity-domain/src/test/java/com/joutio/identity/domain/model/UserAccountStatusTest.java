package com.joutio.identity.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserAccountStatusTest {

    private static final Instant NOW = Instant.parse("2026-11-20T00:00:00Z");
    private static final UserPremium GRANT = new UserPremium(PremiumSource.MANUAL, null, "admin", NOW.minusSeconds(60));

    @Test
    void grant_isPremium() {
        assertTrue(new UserAccountStatus("u1", AccountStatus.ACTIVE, null, GRANT, false).premium(NOW));
    }

    @Test
    void adminWithoutGrant_isPremium() {
        assertTrue(new UserAccountStatus("u1", AccountStatus.ACTIVE, null, null, true).premium(NOW));
    }

    @Test
    void neitherGrantNorAdmin_isNotPremium() {
        assertFalse(new UserAccountStatus("u1", AccountStatus.ACTIVE, null, null, false).premium(NOW));
    }

    @Test
    void suspendedAdmin_isNotPremium() {
        assertFalse(new UserAccountStatus("u1", AccountStatus.SUSPENDED, null, GRANT, true).premium(NOW));
    }
}
