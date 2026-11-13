package com.joutio.identity.domain.port.out;

import java.time.Instant;
import java.util.Optional;

/** Port sortant du parrainage : codes et rattachements filleul → parrain (read-side). */
public interface ReferralPort {

    /** Code du joueur, créé au premier appel (unique, stable). */
    String codeFor(String userId);

    Optional<String> userIdForCode(String code);

    void saveReferral(String userId, String referrerId, Instant referredAt);

    Optional<String> referrerOf(String userId);

    long countReferrals(String referrerId);

    /** Suppression de compte : code et rattachements du joueur (comme filleul et comme parrain). */
    void erase(String userId);
}
