package com.joutio.identity.infrastructure.security;

import com.joutio.identity.domain.model.User;
import com.joutio.identity.domain.port.out.UserRepositoryPort;
import com.joutio.microservice.core.domain.model.security.JoutioPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.time.Instant;

/**
 * Refuse l'émission d'un token (authorization code ou refresh) pour un compte suspendu.
 *
 * <p>Décorateur du générateur standard : les clients M2M ({@code client_credentials}) ne
 * portent pas de {@link JoutioPrincipal} et ne sont donc jamais concernés.</p>
 */
public class SuspendedAccountTokenGenerator implements OAuth2TokenGenerator<OAuth2Token> {

    private final OAuth2TokenGenerator<?> delegate;
    private final UserRepositoryPort userRepositoryPort;

    public SuspendedAccountTokenGenerator(OAuth2TokenGenerator<?> delegate,
                                          UserRepositoryPort userRepositoryPort) {
        this.delegate = delegate;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public OAuth2Token generate(OAuth2TokenContext context) {
        // `OAuth2TokenContext#getPrincipal()` est l'Authentication (ex. UsernamePasswordAuthenticationToken) ;
        // le principal métier Joutio est porté par `Authentication#getPrincipal()`.
        Object principal = context.getPrincipal() == null
                ? null
                : context.getPrincipal().getPrincipal();
        if (principal instanceof JoutioPrincipal joutioPrincipal) {
            User user = userRepositoryPort.findById(joutioPrincipal.getUserId()).orElse(null);
            if (user != null && user.suspended(Instant.now())) {
                throw new OAuth2AuthenticationException(new OAuth2Error(
                        "invalid_grant",
                        "account_suspended",
                        null));
            }
        }
        return (OAuth2Token) delegate.generate(context);
    }
}
