package com.joutio.identity.infrastructure.out.persistence.repository;

import com.joutio.identity.infrastructure.out.persistence.entity.LoginCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginCodeJpaRepository extends JpaRepository<LoginCodeEntity, String> {
}
