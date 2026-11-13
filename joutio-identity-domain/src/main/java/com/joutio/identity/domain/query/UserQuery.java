package com.joutio.identity.domain.query;

import com.joutio.microservice.core.infrastructure.in.api.request.SearchRequest;

import java.util.List;

public interface UserQuery {

    record UserSearchQuery(SearchRequest request) implements UserQuery {}

    record UserExistsByIdQuery(String userId) implements UserQuery {}

    record UserExistsByEmailQuery(String email) implements UserQuery {}

    record GetUserQuery(String userId) implements UserQuery {}

    record FindUserQuery(String userId) implements UserQuery {}

    record FindUserByEmailQuery(String email) implements UserQuery {}

    /** Résolution en lot (recherche admin, enrichissements). */
    record GetUsersByIdsQuery(List<String> userIds) implements UserQuery {}

    /** Statut léger d'un compte, pour le filtre d'accès BFF. */
    record GetUserAccountStatusQuery(String userId) implements UserQuery {}

    /** Statuts en lot, pour les badges publics sans fan-out N+1. */
    record GetUserAccountStatusesQuery(List<String> userIds) implements UserQuery {}

    record GetUserEntitlementsQuery(String userId) implements UserQuery {}

    /** Code de parrainage du joueur (créé au premier appel), nombre de filleuls et parrain éventuel. */
    record GetReferralSummaryQuery(String userId) implements UserQuery {}

    /** Identifiant du joueur qui porte ce code, ou chaîne vide si le code est inconnu. */
    record FindUserIdByReferralCodeQuery(String code) implements UserQuery {}
}
