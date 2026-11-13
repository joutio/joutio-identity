package com.joutio.identity.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.random.RandomGenerator;

/**
 * Règles du parrainage ({@code design/specs/screens/partage.md} § 6).
 *
 * <p>Chaque joueur a un code court, stable et unique ({@value #CODE_LENGTH} caractères sans voyelle ambiguë ni
 * 0/O, 1/I/L). Un filleul applique un code une seule fois, dans les {@link #WINDOW} suivant son inscription, jamais le
 * sien. Le contrôle du parrain (existant, non suspendu) est fait par l'appelant qui prépare la commande.</p>
 */
public final class ReferralRules {

    public static final int CODE_LENGTH = 6;
    public static final String ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    public static final Duration WINDOW = Duration.ofDays(30);

    private ReferralRules() {
    }

    public static String newCode(RandomGenerator random) {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return code.toString();
    }

    /** Code saisi ou lu dans un lien : espaces retirés, majuscules. {@code null} s'il n'a pas la bonne forme. */
    public static String normalizeCode(String value) {
        if (value == null) {
            return null;
        }
        String code = value.replaceAll("\\s", "").toUpperCase(Locale.ROOT);
        if (code.length() != CODE_LENGTH) {
            return null;
        }
        for (char c : code.toCharArray()) {
            if (ALPHABET.indexOf(c) < 0) {
                return null;
            }
        }
        return code;
    }

    public static boolean withinWindow(Instant registeredAt, Instant appliedAt) {
        return registeredAt == null || !appliedAt.isAfter(registeredAt.plus(WINDOW));
    }
}
