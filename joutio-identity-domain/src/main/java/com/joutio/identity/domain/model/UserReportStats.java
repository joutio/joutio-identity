package com.joutio.identity.domain.model;

/**
 * Statistiques de modération des comptes.
 */
public record UserReportStats(
        long openReports,
        long reportedUsers,
        long suspendedUsers
) {
}
