package com.joutio.identity.infrastructure.security;

/** Identité Facebook vérifiée (subject app-scoped + email). */
public record FacebookIdentity(String subject, String email) {
}
