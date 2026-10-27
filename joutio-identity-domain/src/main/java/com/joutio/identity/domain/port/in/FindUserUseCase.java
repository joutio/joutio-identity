package com.joutio.identity.domain.port.in;

import com.joutio.identity.domain.model.User;
import com.joutio.identity.domain.query.UserQuery;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface FindUserUseCase {

    CompletableFuture<Optional<User>> findById(UserQuery.FindUserQuery query);

    CompletableFuture<Optional<User>> findByEmail(UserQuery.FindUserByEmailQuery query);

    default CompletableFuture<Optional<User>> findByEmail(String email) {
        return findByEmail(new UserQuery.FindUserByEmailQuery(email));
    }

     default CompletableFuture<Optional<User>> findById(String id) {
        return findById(new UserQuery.FindUserQuery(id));
    }
}

