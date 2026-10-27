package com.joutio.identity.domain.port.in;

import com.joutio.identity.domain.command.UserCommand;

import java.util.concurrent.CompletableFuture;

/**
 * Port entrant - Cas d'utilisation : enregistrement d'une connexion réussie.
 */
@SuppressWarnings("UnusedReturnValue")
public interface RecordUserLoginUseCase {

    CompletableFuture<Void> recordLogin(UserCommand.RecordUserLoginCommand command);

    default CompletableFuture<Void> recordLogin(String userId) {
        return recordLogin(new UserCommand.RecordUserLoginCommand(userId));
    }
}
