package com.joutio.identity.infrastructure.security;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.joutio.identity.domain.model.User;
import com.joutio.identity.domain.port.out.UserRepositoryPort;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.*;

class UserEntitlementsAdapterTest {
    @Test
    void usesSameRolesAsTokenIssuance() {
        var users = mock(UserRepositoryPort.class);
        var roles = mock(Roles.class);
        var user = new User("user", "user@example.test", Set.of(), Instant.now());
        when(users.findById("user")).thenReturn(Optional.of(user));
        when(roles.forUser("user", "user@example.test"))
                .thenReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertThat(new UserEntitlementsAdapter(users, roles).get("user").premium()).isTrue();
        verify(roles).forUser("user", "user@example.test");
    }

    @Test
    void suspendedAccountCannotExercisePremium() {
        var users = mock(UserRepositoryPort.class);
        var roles = mock(Roles.class);
        var user = mock(User.class);
        when(users.findById("user")).thenReturn(Optional.of(user));
        when(user.suspended(any(Instant.class))).thenReturn(true);
        assertThat(new UserEntitlementsAdapter(users, roles).get("user").premium()).isFalse();
        verifyNoInteractions(roles);
    }
}
