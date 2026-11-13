package com.joutio.identity.domain.model;

/**
 * Parrainage vu par le joueur : son code et le nombre de filleuls rattachés. Les filleuls « qualifiés » (premier
 * duel terminé) sont comptés par {@code joutio-profile} (badge Recruteur).
 *
 * @param referredBy parrain du joueur, s'il en a un
 */
public record ReferralSummary(String userId, String code, long invited, String referredBy) {
}
