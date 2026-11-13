package com.joutio.identity.infrastructure.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** Code de parrainage d'un joueur : unique, stable, créé à la première demande. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "referral_code")
public class ReferralCodeEntity {

    @Id
    @Column(name = "user_id", length = 255, nullable = false)
    private String userId;

    @Column(name = "code", length = 6, nullable = false, unique = true)
    private String code;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public ReferralCodeEntity(String userId, String code, Instant createdAt) {
        this.userId = userId;
        this.code = code;
        this.createdAt = createdAt;
    }
}
