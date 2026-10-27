package com.joutio.identity.infrastructure.security;

import com.joutio.identity.infrastructure.properties.AppProperties;
import com.joutio.microservice.core.domain.constant.SecurityConstants;
import com.joutio.microservice.core.domain.model.security.JoutioPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

/**
 * Customizer pour ajouter des claims personnalisés aux JWT
 */
@Component
public class JwtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final String audience;

    public JwtTokenCustomizer(AppProperties properties) {
        this.audience = properties.authorizationServer().audience();
    }

    @Override
    public void customize(JwtEncodingContext context) {
        Authentication authentication = context.getPrincipal();

        if (authentication.getPrincipal() instanceof JoutioPrincipal joutioPrincipal) {
            context.getClaims().claims(claims -> {
                // Claims standards
                claims.put("sub", joutioPrincipal.getUserId());
                claims.put("email", joutioPrincipal.getEmail());

                // Claims personnalisés Joutio
                claims.put(SecurityConstants.USER_EMAIL_CLAIM, joutioPrincipal.getEmail());
                claims.put(SecurityConstants.USER_IDENTIFIER_CLAIM, joutioPrincipal.getUserId());

                // Audience partagée par tous les types de tokens
                if (StringUtils.hasText(audience)) {
                    claims.put("aud", audience);
                }

                // Rôles
                claims.put("roles", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));
            });
        }
    }
}
