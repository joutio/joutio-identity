package com.joutio.identity.infrastructure.out.persistence.repository;

import com.joutio.identity.infrastructure.out.persistence.entity.ReferralEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralJpaRepository extends JpaRepository<ReferralEntity, String> {

    long countByReferrerId(String referrerId);

    void deleteByReferrerId(String referrerId);
}
