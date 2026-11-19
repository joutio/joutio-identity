package com.joutio.identity.domain.model;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Règles pures des signalements de joueurs et des suspensions de compte. Les contrôles renvoient la violation
 * constatée ; l'appelant lève le problème typé de son contexte (signalement, suspension, Premium).
 */
public interface UserReportRules {

    int MAX_COMMENT_LENGTH = 500;
    int MAX_NOTE_LENGTH = 500;
    int MAX_REASONS = UserReportReason.values().length;

    /** Identifiant déterministe du thread (joueur signalé × signalant). */
    static String threadId(String targetUserId, String reporterId) {
        String seed = Objects.requireNonNull(targetUserId, "targetUserId") + "|"
                + Objects.requireNonNull(reporterId, "reporterId");
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)).toString();
    }

    static Optional<String> reportViolation(Set<UserReportReason> reasons, String comment) {
        if (reasons == null || reasons.isEmpty()) {
            return Optional.of("At least one report reason is required");
        }
        if (reasons.size() > MAX_REASONS) {
            return Optional.of("Too many report reasons");
        }
        if (comment != null && comment.length() > MAX_COMMENT_LENGTH) {
            return Optional.of("Report comment is too long");
        }
        return Optional.empty();
    }

    static Optional<String> noteViolation(String note) {
        return note != null && note.length() > MAX_NOTE_LENGTH
                ? Optional.of("Note is too long")
                : Optional.empty();
    }

    static Optional<String> suspensionViolation(Instant suspendedAt, Instant suspendedUntil) {
        if (suspendedAt == null) {
            return Optional.of("A suspension date is required");
        }
        return suspendedUntil != null && !suspendedUntil.isAfter(suspendedAt)
                ? Optional.of("Suspension end must be in the future")
                : Optional.empty();
    }

    static Optional<String> selfReportViolation(String targetUserId, String reporterId) {
        return Objects.equals(targetUserId, reporterId)
                ? Optional.of("A player cannot report themselves")
                : Optional.empty();
    }
}
