package com.joutio.identity.application.handler.query;

import com.joutio.identity.domain.model.UserEntitlements;
import com.joutio.identity.domain.port.out.UserEntitlementsPort;
import com.joutio.identity.domain.query.UserQuery;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserEntitlementsQueryHandler {
    private final UserEntitlementsPort entitlements;

    public UserEntitlementsQueryHandler(UserEntitlementsPort entitlements) {
        this.entitlements = entitlements;
    }

    @QueryHandler
    public UserEntitlements handle(UserQuery.GetUserEntitlementsQuery query) {
        return entitlements.get(query.userId());
    }
}
