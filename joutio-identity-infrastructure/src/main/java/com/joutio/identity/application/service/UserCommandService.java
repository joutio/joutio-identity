package com.joutio.identity.application.service;

import com.joutio.identity.domain.port.in.RegisterUserUseCase;
import com.joutio.identity.domain.port.in.LinkSocialProviderUseCase;
import com.joutio.identity.domain.port.in.RecordUserLoginUseCase;
import com.joutio.identity.domain.command.UserCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service applicatif - Implémente RegisterUserUseCase.
 */
@Service
public class UserCommandService implements RegisterUserUseCase, LinkSocialProviderUseCase, RecordUserLoginUseCase {

    private final CommandGateway commandGateway;

    public UserCommandService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public CompletableFuture<String> registerUser(UserCommand.RegisterUserCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> registerWithSocial(UserCommand.RegisterUserWithSocialCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<Void> linkSocialProvider(UserCommand.LinkSocialProviderCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<Void> recordLogin(UserCommand.RecordUserLoginCommand command) {
        return commandGateway.send(command);
    }
}

