package com.joutio.identity.infrastructure.security;

import com.joutio.identity.domain.exception.UserProblems;
import com.joutio.identity.domain.model.UserEntitlements;
import com.joutio.identity.domain.port.out.UserEntitlementsPort;
import com.joutio.identity.domain.port.out.UserRepositoryPort;

import org.springframework.stereotype.Component;

@Component
public class UserEntitlementsAdapter implements UserEntitlementsPort {
    private final UserRepositoryPort users;
    private final Roles roles;

    public UserEntitlementsAdapter(UserRepositoryPort users, Roles roles) {
        this.users = users;
        this.roles = roles;
    }

    public UserEntitlements get(String userId) {
        var user =
                users.findById(userId)
                        .orElseThrow(() -> new UserProblems.UserNotFoundProblem(userId));
        boolean premium =
                !user.suspended(java.time.Instant.now())
                        && roles.forUser(userId, user.email()).stream()
                                .anyMatch(
                                        role ->
                                                role.getAuthority().equals("ROLE_ADMIN")
                                                        || role.getAuthority()
                                                                .equals("ROLE_PREMIUM"));
        return new UserEntitlements(userId, premium);
    }
}
