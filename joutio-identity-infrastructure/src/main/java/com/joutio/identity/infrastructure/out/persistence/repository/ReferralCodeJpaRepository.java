package com.joutio.identity.infrastructure.out.persistence.repository;

import com.joutio.identity.infrastructure.out.persistence.entity.ReferralCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferralCodeJpaRepository extends JpaRepository<ReferralCodeEntity, String> {

    Optional<ReferralCodeEntity> findByCode(String code);
}
