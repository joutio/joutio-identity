package com.joutio.identity.domain.model;

import java.time.Instant;

/**
 * Compteurs de signalements pour un joueur.
 */
public record UserReportCount(
        String targetUserId,
        int openCount,
        int totalCount,
        Instant lastReportedAt
) {
}
