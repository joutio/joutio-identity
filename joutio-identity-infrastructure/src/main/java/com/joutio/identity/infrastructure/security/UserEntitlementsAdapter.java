package com.joutio.identity.infrastructure.security;

import com.joutio.identity.domain.exception.UserProblems;
import com.joutio.identity.domain.model.UserEntitlements;
import com.joutio.identity.domain.model.UserPremium;
import com.joutio.identity.domain.port.out.UserEntitlementsPort;
import com.joutio.identity.domain.port.out.UserRepositoryPort;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

/**
 * Droits effectifs d'un compte : Premium accordé dans {@code UserAggregate} (non échu) ou
 * {@code ROLE_ADMIN} ; jamais pour un compte suspendu.
 */
@Component
public class UserEntitlementsAdapter implements UserEntitlementsPort {
    private final UserRepositoryPort users;
    private final Roles roles;
    private final Clock clock;

    public UserEntitlementsAdapter(UserRepositoryPort users, Roles roles) {
        this(users, roles, Clock.systemUTC());
    }

    UserEntitlementsAdapter(UserRepositoryPort users, Roles roles, Clock clock) {
        this.users = users;
        this.roles = roles;
        this.clock = clock;
    }

    public UserEntitlements get(String userId) {
        var user =
                users.findById(userId)
                        .orElseThrow(() -> new UserProblems.UserNotFoundProblem(userId));
        Instant now = clock.instant();
        UserPremium granted = user.premium();
        if (user.suspended(now)) {
            return new UserEntitlements(userId, false, null, null);
        }
        boolean premium =
                user.premiumActive(now)
                        || roles.forUser(userId, user.email()).stream()
                                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
        return new UserEntitlements(
                userId,
                premium,
                granted == null ? null : granted.premiumUntil(),
                granted == null ? null : granted.source());
    }
}
