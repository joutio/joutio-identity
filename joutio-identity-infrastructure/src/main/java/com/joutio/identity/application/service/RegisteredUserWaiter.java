package com.joutio.identity.application.service;

import com.joutio.identity.domain.port.out.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Attend qu'un compte tout juste inscrit soit lisible dans {@code user_entry} avant d'émettre le
 * jeton de session.
 *
 * <p>L'inscription est une commande ; {@code user_entry} est écrit ensuite par {@code user-projection}
 * (événement → Kafka → projection, possiblement sur un autre pod). Sans cette attente, la première
 * requête du client arrive au BFF avant la projection, qui rejette la session comme celle d'un compte
 * supprimé ({@code CurrentUserExistsFilter}).</p>
 */
@Component
public class RegisteredUserWaiter {

    private static final Logger logger = LoggerFactory.getLogger(RegisteredUserWaiter.class);
    static final Duration POLL_INTERVAL = Duration.ofMillis(50);
    static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final UserRepositoryPort userRepositoryPort;

    public RegisteredUserWaiter(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    /** Rend la main dès que le compte est lisible, ou après {@link #TIMEOUT} (journalisé). */
    public void awaitReadable(String userId) {
        long deadline = System.nanoTime() + TIMEOUT.toNanos();
        while (!userRepositoryPort.existsById(userId)) {
            if (System.nanoTime() >= deadline) {
                logger.warn("Compte {} non projeté après {} : jeton émis quand même", userId, TIMEOUT);
                return;
            }
            try {
                Thread.sleep(POLL_INTERVAL.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
