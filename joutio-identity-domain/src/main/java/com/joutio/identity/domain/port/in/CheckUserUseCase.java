package com.joutio.identity.domain.port.in;

import com.joutio.identity.domain.query.UserQuery;

import java.util.concurrent.CompletableFuture;

/**
 * Port entrant - Cas d'utilisation : verifications d'existence utilisateur.
 * Utilise par DataSeeder et potentiellement d'autres services.
 */
public interface CheckUserUseCase {

    CompletableFuture<Boolean> existsById(UserQuery.UserExistsByIdQuery query);

    CompletableFuture<Boolean> existsByEmail(UserQuery.UserExistsByEmailQuery query);

    default CompletableFuture<Boolean> existsById(String userId) {
        return existsById(new UserQuery.UserExistsByIdQuery(userId));
    }

    default CompletableFuture<Boolean> existsByEmail(String email) {
        return existsByEmail(new UserQuery.UserExistsByEmailQuery(email));
    }
}

