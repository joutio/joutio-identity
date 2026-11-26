package com.joutio.identity.infrastructure.out.persistence.adapter;

import com.joutio.identity.domain.port.out.LiveVersionPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Version du compte surveillé ({@code live_version}) : un upsert atomique, qui verrouille la ligne jusqu'à la fin de
 * la transaction. Deux écritures concurrentes du même compte obtiennent donc deux versions successives.
 */
@Component
public class LiveVersionAdapter implements LiveVersionPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public long increment(String userId) {
        Number version = (Number) entityManager.createNativeQuery("""
                        INSERT INTO live_version (user_id, version) VALUES (:userId, 1)
                        ON CONFLICT (user_id) DO UPDATE SET version = live_version.version + 1
                        RETURNING version""")
                .setParameter("userId", userId)
                .getSingleResult();
        return version.longValue();
    }
}
