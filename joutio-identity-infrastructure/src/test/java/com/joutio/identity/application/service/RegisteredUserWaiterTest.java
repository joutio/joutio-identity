package com.joutio.identity.application.service;

import com.joutio.identity.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisteredUserWaiterTest {

    private final UserRepositoryPort users = mock(UserRepositoryPort.class);
    private final RegisteredUserWaiter waiter = new RegisteredUserWaiter(users);

    @Test
    void returnsAtOnceWhenTheAccountIsAlreadyProjected() {
        when(users.existsById("u-1")).thenReturn(true);

        waiter.awaitReadable("u-1");

        verify(users, times(1)).existsById("u-1");
    }

    @Test
    void waitsUntilTheProjectionWritesTheAccount() {
        when(users.existsById("u-1")).thenReturn(false, false, true);

        waiter.awaitReadable("u-1");

        verify(users, times(3)).existsById("u-1");
    }
}
