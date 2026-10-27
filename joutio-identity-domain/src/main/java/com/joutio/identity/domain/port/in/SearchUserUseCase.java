package com.joutio.identity.domain.port.in;

import com.joutio.microservice.core.infrastructure.in.api.request.SearchRequest;
import com.joutio.microservice.core.infrastructure.in.api.response.SearchResponse;
import com.joutio.identity.domain.model.User;
import com.joutio.identity.domain.query.UserQuery;

import java.util.concurrent.CompletableFuture;

public interface SearchUserUseCase {

    CompletableFuture<SearchResponse<User>> search(UserQuery.UserSearchQuery query);

    default CompletableFuture<SearchResponse<User>> search(SearchRequest request) {
        return search(new UserQuery.UserSearchQuery(request));
    }
}
