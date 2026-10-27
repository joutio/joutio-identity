package com.joutio.identity.domain.port.in;

import com.joutio.identity.domain.exception.UserProblems;
import com.joutio.identity.domain.model.User;
import com.joutio.identity.domain.query.UserQuery;

import java.util.concurrent.CompletableFuture;

/**
 * Port entrant - Cas d'utilisation : recuperation d'un utilisateur par ID.
 * Implémente par UserQueryService dans application/service/
 */
public interface GetUserUseCase {

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param query query contenant l'identifiant de l'utilisateur
     * @return un CompletableFuture contenant un utilisateur
     * @throws UserProblems.UserNotFoundProblem si l'utilisateur n'existe pas
     */
    CompletableFuture<User> getById(UserQuery.GetUserQuery query) throws UserProblems.UserNotFoundProblem;

    default CompletableFuture<User> getById(String id) throws UserProblems.UserNotFoundProblem {
        return getById(new UserQuery.GetUserQuery(id));
    }
}

