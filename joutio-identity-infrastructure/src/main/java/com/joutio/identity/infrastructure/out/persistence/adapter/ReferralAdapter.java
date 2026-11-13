package com.joutio.identity.infrastructure.out.persistence.adapter;

import com.joutio.identity.domain.model.ReferralRules;
import com.joutio.identity.domain.port.out.ReferralPort;
import com.joutio.identity.infrastructure.out.persistence.entity.ReferralCodeEntity;
import com.joutio.identity.infrastructure.out.persistence.entity.ReferralEntity;
import com.joutio.identity.infrastructure.out.persistence.repository.ReferralCodeJpaRepository;
import com.joutio.identity.infrastructure.out.persistence.repository.ReferralJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

/**
 * Adapter du parrainage. Le code est tiré au hasard puis inséré immédiatement ({@code saveAndFlush}) : une
 * collision (contrainte unique) ou une création concurrente pour le même joueur se résout par un nouvel essai.
 */
@Component
public class ReferralAdapter implements ReferralPort {

    private static final int ATTEMPTS = 8;

    private final ReferralCodeJpaRepository codes;
    private final ReferralJpaRepository referrals;
    private final SecureRandom random = new SecureRandom();

    public ReferralAdapter(ReferralCodeJpaRepository codes, ReferralJpaRepository referrals) {
        this.codes = codes;
        this.referrals = referrals;
    }

    @Override
    public String codeFor(String userId) {
        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            Optional<ReferralCodeEntity> existing = codes.findById(userId);
            if (existing.isPresent()) {
                return existing.get().getCode();
            }
            try {
                return codes.saveAndFlush(new ReferralCodeEntity(userId, ReferralRules.newCode(random), Instant.now())).getCode();
            } catch (DataIntegrityViolationException collision) {
                // Code déjà pris, ou code créé entre-temps pour ce joueur : on relit puis on réessaie.
            }
        }
        throw new IllegalStateException("Unable to allocate a referral code for " + userId);
    }

    @Override
    public Optional<String> userIdForCode(String code) {
        return codes.findByCode(code).map(ReferralCodeEntity::getUserId);
    }

    @Override
    public void saveReferral(String userId, String referrerId, Instant referredAt) {
        referrals.save(new ReferralEntity(userId, referrerId, referredAt));
    }

    @Override
    public Optional<String> referrerOf(String userId) {
        return referrals.findById(userId).map(ReferralEntity::getReferrerId);
    }

    @Override
    public long countReferrals(String referrerId) {
        return referrals.countByReferrerId(referrerId);
    }

    @Override
    @Transactional
    public void erase(String userId) {
        codes.deleteById(userId);
        referrals.deleteById(userId);
        referrals.deleteByReferrerId(userId);
    }
}
