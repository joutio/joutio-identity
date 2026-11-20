package com.joutio.identity.infrastructure.security;

import com.joutio.identity.domain.exception.UserProblems;
import com.joutio.identity.domain.model.UserEntitlements;
import com.joutio.identity.domain.model.UserPremium;
import com.joutio.identity.domain.port.out.AdminAccountsPort;
import com.joutio.identity.domain.port.out.UserEntitlementsPort;
import com.joutio.identity.domain.port.out.UserRepositoryPort;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final AdminAccountsPort adminAccounts;
    private final Clock clock;

    @Autowired
    public UserEntitlementsAdapter(UserRepositoryPort users, AdminAccountsPort adminAccounts) {
        this(users, adminAccounts, Clock.systemUTC());
    }

    UserEntitlementsAdapter(UserRepositoryPort users, AdminAccountsPort adminAccounts, Clock clock) {
        this.users = users;
        this.adminAccounts = adminAccounts;
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
                user.premiumActive(now) || adminAccounts.isAdmin(userId, user.email());
        return new UserEntitlements(
                userId,
                premium,
                granted == null ? null : granted.premiumUntil(),
                granted == null ? null : granted.source());
    }
}
