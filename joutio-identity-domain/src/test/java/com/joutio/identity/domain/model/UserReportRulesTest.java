package com.joutio.identity.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserReportRulesTest {

    @Test
    void reportViolation_requiresAtLeastOneReason() {
        assertTrue(UserReportRules.reportViolation(Set.of(), null).isPresent());
    }

    @Test
    void reportViolation_acceptsReasonsAndBoundedComment() {
        assertTrue(UserReportRules.reportViolation(
                Set.of(UserReportReason.SPAM), "x".repeat(UserReportRules.MAX_COMMENT_LENGTH)).isEmpty());
    }

    @Test
    void reportViolation_rejectsTooLongComment() {
        assertTrue(UserReportRules.reportViolation(
                Set.of(UserReportReason.SPAM), "x".repeat(UserReportRules.MAX_COMMENT_LENGTH + 1)).isPresent());
    }

    @Test
    void suspensionViolation_rejectsPastEnd() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        assertTrue(UserReportRules.suspensionViolation(now, now.minusSeconds(1)).isPresent());
        assertTrue(UserReportRules.suspensionViolation(now, null).isEmpty());
        assertTrue(UserReportRules.suspensionViolation(now, now.plusSeconds(1)).isEmpty());
    }

    @Test
    void noteViolation_rejectsTooLongNote() {
        assertTrue(UserReportRules.noteViolation("x".repeat(UserReportRules.MAX_NOTE_LENGTH + 1)).isPresent());
        assertTrue(UserReportRules.noteViolation(null).isEmpty());
    }

    @Test
    void accountStatus_expiredSuspensionIsEffectivelyActive() {
        Instant now = Instant.parse("2026-01-02T00:00:00Z");
        assertFalse(AccountStatusRules.isSuspended(
                AccountStatus.SUSPENDED, now.minusSeconds(1), now));
        assertTrue(AccountStatusRules.isSuspended(
                AccountStatus.SUSPENDED, now.plusSeconds(1), now));
        assertTrue(AccountStatusRules.isSuspended(AccountStatus.SUSPENDED, null, now));
    }
}
