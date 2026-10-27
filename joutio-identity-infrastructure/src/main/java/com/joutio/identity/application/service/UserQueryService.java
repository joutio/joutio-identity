package com.joutio.identity.application.service;

import com.joutio.microservice.core.infrastructure.axon.QueryResponseTypes;
import com.joutio.microservice.core.infrastructure.in.api.response.SearchResponse;
import com.joutio.identity.domain.exception.UserProblems;
import com.joutio.identity.domain.model.User;
import com.joutio.identity.domain.port.in.CheckUserUseCase;
import com.joutio.identity.domain.port.in.FindUserUseCase;
import com.joutio.identity.domain.port.in.GetUserUseCase;
import com.joutio.identity.domain.port.in.SearchUserUseCase;
import com.joutio.identity.domain.query.UserQuery;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service applicatif - Implémente GetUserUseCase.
 * Cette classe est autorisée à connaitre QueryGateway (couche application).
 */
@Service
public class UserQueryService implements GetUserUseCase, FindUserUseCase, CheckUserUseCase, SearchUserUseCase {

    private final QueryGateway queryGateway;

    public UserQueryService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public CompletableFuture<Boolean> existsById(UserQuery.UserExistsByIdQuery query) {
        return queryGateway.query(query, QueryResponseTypes.instanceOf(Boolean.class));
    }

    @Override
    public CompletableFuture<Boolean> existsByEmail(UserQuery.UserExistsByEmailQuery query) {
        return queryGateway.query(query, QueryResponseTypes.instanceOf(Boolean.class));
    }

    @Override
    public CompletableFuture<Optional<User>> findById(UserQuery.FindUserQuery query) {
        return queryGateway.query(query, QueryResponseTypes.optionalInstanceOf(User.class));
    }

    @Override
    public CompletableFuture<Optional<User>> findByEmail(UserQuery.FindUserByEmailQuery query) {
        return queryGateway.query(query, QueryResponseTypes.optionalInstanceOf(User.class));
    }

    @Override
    public CompletableFuture<User> getById(UserQuery.GetUserQuery query) throws UserProblems.UserNotFoundProblem {
        return queryGateway.query(query, QueryResponseTypes.instanceOf(User.class));
    }

    @Override
    public CompletableFuture<SearchResponse<User>> search(UserQuery.UserSearchQuery query) {
        return queryGateway.query(query, QueryResponseTypes.searchResponseOf(User.class));
    }
}

