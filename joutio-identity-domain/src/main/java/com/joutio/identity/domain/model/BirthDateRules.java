package com.joutio.identity.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Règles de la date de naissance (privée, jamais exposée hors du joueur concerné).
 *
 * <p>Joutio est réservé aux {@value #MIN_AGE} ans et plus. L'âge se calcule sur le jour UTC de la demande ;
 * un né un 29 février atteint son anniversaire le 28 février des années non bissextiles
 * ({@link LocalDate#plusYears(long)}).</p>
 */
public final class BirthDateRules {

    public static final int MIN_AGE = 15;
    public static final int MAX_AGE = 120;

    private BirthDateRules() {
    }

    /** Jour UTC de l'instant. */
    public static LocalDate today(Instant at) {
        return LocalDate.ofInstant(at, ZoneOffset.UTC);
    }

    public static long age(LocalDate birthDate, LocalDate today) {
        return ChronoUnit.YEARS.between(birthDate, today);
    }

    /** Violation constatée : date absente, future ou au-delà de {@value #MAX_AGE} ans. */
    public static Optional<String> violation(LocalDate birthDate, LocalDate today) {
        if (birthDate == null) {
            return Optional.of("A birth date is required");
        }
        if (birthDate.isAfter(today)) {
            return Optional.of("The birth date must not be in the future");
        }
        if (age(birthDate, today) > MAX_AGE) {
            return Optional.of("The birth date must be less than " + MAX_AGE + " years ago");
        }
        return Optional.empty();
    }

    public static boolean oldEnough(LocalDate birthDate, LocalDate today) {
        return !birthDate.plusYears(MIN_AGE).isAfter(today);
    }

    /**
     * La date est exigée des comptes créés à partir de {@code requiredSince} (mise en service) ; les comptes
     * plus anciens la renseignent sans blocage.
     */
    public static boolean required(User user, Instant requiredSince) {
        return user.birthDate() == null
                && requiredSince != null
                && user.createdAt() != null
                && !user.createdAt().isBefore(requiredSince);
    }
}
