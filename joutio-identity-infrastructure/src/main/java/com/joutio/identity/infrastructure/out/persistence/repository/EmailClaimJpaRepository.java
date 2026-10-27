package com.joutio.identity.infrastructure.out.persistence.repository;

import com.joutio.identity.infrastructure.out.persistence.entity.EmailClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailClaimJpaRepository extends JpaRepository<EmailClaimEntity, String> {
}
