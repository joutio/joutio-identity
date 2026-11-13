package com.joutio.identity.infrastructure.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** Rattachement filleul → parrain (projection de {@code UserReferredEvent}). */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "referral_entry")
public class ReferralEntity {

    @Id
    @Column(name = "user_id", length = 255, nullable = false)
    private String userId;

    @Column(name = "referrer_id", length = 255, nullable = false)
    private String referrerId;

    @Column(name = "referred_at", nullable = false)
    private Instant referredAt;

    public ReferralEntity(String userId, String referrerId, Instant referredAt) {
        this.userId = userId;
        this.referrerId = referrerId;
        this.referredAt = referredAt;
    }
}
