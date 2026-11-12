package com.joutio.identity.infrastructure.security;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.joutio.identity.domain.model.AccountStatus;
import com.joutio.identity.domain.model.PremiumSource;
import com.joutio.identity.domain.model.User;
import com.joutio.identity.domain.model.UserPremium;
import com.joutio.identity.domain.model.UserSuspension;
import com.joutio.identity.domain.model.UserSuspensionReason;
import com.joutio.identity.domain.port.out.UserRepositoryPort;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

class UserEntitlementsAdapterTest {

    private static final Instant NOW = Instant.parse("2026-11-12T10:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    private final UserRepositoryPort users = mock(UserRepositoryPort.class);
    private final Roles roles = mock(Roles.class);
    private final UserEntitlementsAdapter adapter = new UserEntitlementsAdapter(users, roles, CLOCK);

    @Test
    void adminIsPremiumWithoutGrant() {
        stub(user(AccountStatus.ACTIVE, null, null));
        when(roles.forUser("user", "user@example.test"))
                .thenReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertThat(adapter.get("user").premium()).isTrue();
    }

    @Test
    void manualGrantIsPremiumAndExposesEnd() {
        Instant until = NOW.plusSeconds(86_400);
        stub(user(AccountStatus.ACTIVE, null, grant(until)));
        var entitlements = adapter.get("user");
        assertThat(entitlements.premium()).isTrue();
        assertThat(entitlements.premiumUntil()).isEqualTo(until);
        assertThat(entitlements.source()).isEqualTo(PremiumSource.MANUAL);
        verifyNoInteractions(roles);
    }

    @Test
    void expiredGrantIsNotPremium() {
        stub(user(AccountStatus.ACTIVE, null, grant(NOW.minusSeconds(1))));
        when(roles.forUser(any(), any())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));
        assertThat(adapter.get("user").premium()).isFalse();
    }

    @Test
    void suspendedAccountCannotExercisePremium() {
        UserSuspension suspension = new UserSuspension(
                "s-1", UserSuspensionReason.SPAM, null, "admin", NOW.minusSeconds(60), null,
                null, null, null, null);
        stub(user(AccountStatus.SUSPENDED, suspension, grant(null)));
        assertThat(adapter.get("user").premium()).isFalse();
        verifyNoInteractions(roles);
    }

    /** Deux constructeurs : Spring doit savoir lequel injecter (sinon « No default constructor found »). */
    @Test
    void springConstructorIsDesignated() {
        long designated = Arrays.stream(UserEntitlementsAdapter.class.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(
                        org.springframework.beans.factory.annotation.Autowired.class))
                .count();
        assertThat(designated).isEqualTo(1);
    }

    private void stub(User user) {
        when(users.findById("user")).thenReturn(Optional.of(user));
    }

    private static User user(AccountStatus status, UserSuspension suspension, UserPremium premium) {
        return new User("user", "user@example.test", Set.of(), NOW.minusSeconds(86_400 * 30L),
                status, suspension, premium);
    }

    private static UserPremium grant(Instant until) {
        return new UserPremium(PremiumSource.MANUAL, until, "admin", NOW.minusSeconds(3600));
    }
}
