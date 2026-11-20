package com.joutio.identity.infrastructure.security;

import com.joutio.identity.domain.exception.UserProblems;
import com.joutio.identity.domain.model.UserEntitlements;
import com.joutio.identity.domain.model.UserPremium;
import com.joutio.identity.domain.port.out.UserEntitlementsPort;
import com.joutio.identity.domain.port.out.UserRepositoryPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

/**
 * Droits effectifs d'un compte : Premium accordé dans {@code UserAggregate} (non échu), jamais pour un compte
 * suspendu. {@code ROLE_ADMIN} ne donne pas le Premium : un admin se l'accorde comme tout autre compte.
 */
@Component
public class UserEntitlementsAdapter implements UserEntitlementsPort {
    private final UserRepositoryPort users;
    private final Clock clock;

    @Autowired
    public UserEntitlementsAdapter(UserRepositoryPort users) {
        this(users, Clock.systemUTC());
    }

    UserEntitlementsAdapter(UserRepositoryPort users, Clock clock) {
        this.users = users;
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
        return new UserEntitlements(
                userId,
                user.premiumActive(now),
                granted == null ? null : granted.premiumUntil(),
                granted == null ? null : granted.source());
    }
}
