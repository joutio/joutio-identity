package com.joutio.identity.domain.model;

import java.time.Instant;

/**
 * Droits effectifs d'un compte. {@code premium} tient compte de la suspension et de
 * {@code ROLE_ADMIN} ; {@code premiumUntil} et {@code source} décrivent le droit accordé, s'il existe.
 */
public record UserEntitlements(String userId, boolean premium, Instant premiumUntil, PremiumSource source) {

    /** Constructeur de compatibilité (droit sans détail). */
    public UserEntitlements(String userId, boolean premium) {
        this(userId, premium, null, null);
    }
}
