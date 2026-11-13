package com.joutio.identity.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BirthDateRulesTest {

    private static final Instant ROLLOUT = Instant.parse("2026-11-20T00:00:00Z");

    @Test
    void today_isUtcDay() {
        assertEquals(LocalDate.parse("2026-11-13"), BirthDateRules.today(Instant.parse("2026-11-13T23:30:00Z")));
    }

    @Test
    void required_onlyForAccountsCreatedAfterRollout() {
        User before = new User("u1", "a@b.c", Set.of(), Instant.parse("2026-11-01T00:00:00Z"));
        User after = new User("u2", "a@b.c", Set.of(), Instant.parse("2026-11-21T00:00:00Z"));
        assertFalse(BirthDateRules.required(before, ROLLOUT));
        assertTrue(BirthDateRules.required(after, ROLLOUT));
        assertFalse(BirthDateRules.required(after.withBirthDate(LocalDate.parse("2000-01-01")), ROLLOUT));
        assertFalse(BirthDateRules.required(after, null));
    }
}
