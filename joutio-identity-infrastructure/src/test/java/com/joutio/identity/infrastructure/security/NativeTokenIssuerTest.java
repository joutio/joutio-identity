package com.joutio.identity.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NativeTokenIssuerTest {

    @Test
    void issuesAccessAndRefreshTokensForPublicMobileClient() {
        RegisteredClientRepository clients = mock(RegisteredClientRepository.class);
        OAuth2TokenGenerator<?> generator = mock(OAuth2TokenGenerator.class);
        OAuth2AuthorizationService authorizations = mock(OAuth2AuthorizationService.class);

        RegisteredClient mobile = RegisteredClient.withId("mobile-id")
                .clientId("mobile")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("joutio://callback")
                .scope("openid")
                .scope("profile")
                .scope("email")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .refreshTokenTimeToLive(Duration.ofDays(365))
                        .reuseRefreshTokens(false)
                        .build())
                .build();
        when(clients.findByClientId("mobile")).thenReturn(mobile);

        Instant now = Instant.now();
        when(generator.generate(any(OAuth2TokenContext.class))).thenAnswer(invocation -> {
            OAuth2TokenContext context = invocation.getArgument(0);
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                return Jwt.withTokenValue("access-token")
                        .header("alg", "RS256")
                        .subject("user-1")
                        .issuedAt(now)
                        .expiresAt(now.plusSeconds(3600))
                        .claim("user_id", "user-1")
                        .build();
            }
            if (OAuth2TokenType.REFRESH_TOKEN.equals(context.getTokenType())) {
                return new OAuth2RefreshToken("refresh-token", now, now.plus(Duration.ofDays(365)));
            }
            return null;
        });

        NativeTokenIssuer issuer = new NativeTokenIssuer(
                clients,
                generator,
                authorizations,
                AuthorizationServerSettings.builder().issuer("http://localhost:8085").build()
        );

        UserPrincipal user = new UserPrincipal(
                "user-1",
                "player@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                user, null, user.getAuthorities());

        NativeTokenIssuer.IssuedTokens tokens = issuer.issue(authentication, "mobile");

        assertThat(tokens.accessToken()).isEqualTo("access-token");
        assertThat(tokens.refreshToken()).isEqualTo("refresh-token");
        assertThat(tokens.tokenType()).isEqualTo(OAuth2AccessToken.TokenType.BEARER.getValue());
        assertThat(tokens.expiresIn()).isEqualTo(3600);

        ArgumentCaptor<OAuth2Authorization> captor = ArgumentCaptor.forClass(OAuth2Authorization.class);
        verify(authorizations).save(captor.capture());
        OAuth2Authorization authorization = captor.getValue();

        assertThat(authorization.getPrincipalName()).isEqualTo("user-1");
        assertThat(authorization.getAuthorizationGrantType()).isEqualTo(NativeTokenIssuer.NATIVE_GRANT_TYPE);
        assertThat(authorization.getAuthorizedScopes()).containsExactlyInAnyOrder("profile", "email");
        assertThat(authorization.getAuthorizedScopes()).doesNotContain("openid");
        assertThat(authorization.getAccessToken()).isNotNull();
        assertThat(authorization.getAccessToken().getToken().getTokenValue()).isEqualTo("access-token");
        assertThat(authorization.getRefreshToken()).isNotNull();
        assertThat(authorization.getRefreshToken().getToken().getTokenValue()).isEqualTo("refresh-token");
        Object principalAttribute = authorization.getAttribute(Principal.class.getName());
        assertThat(principalAttribute).isSameAs(authentication);
    }

    /**
     * Régression : les scopes passés au générateur finissent dans le claim `scope`, stocké avec l'autorisation et
     * relu au refresh. Ils doivent passer l'allowlist Jackson du stockage JDBC (sinon `/oauth2/token` répond 500).
     */
    @Test
    void authorizedScopesSurviveTheJdbcRoundTripUsedByRefresh() throws Exception {
        RegisteredClientRepository clients = mock(RegisteredClientRepository.class);
        OAuth2TokenGenerator<?> generator = mock(OAuth2TokenGenerator.class);
        RegisteredClient mobile = RegisteredClient.withId("mobile-id")
                .clientId("mobile")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("joutio://callback")
                .scope("openid")
                .scope("profile")
                .scope("email")
                .build();
        when(clients.findByClientId("mobile")).thenReturn(mobile);

        Instant now = Instant.now();
        java.util.concurrent.atomic.AtomicReference<Object> scopes = new java.util.concurrent.atomic.AtomicReference<>();
        when(generator.generate(any(OAuth2TokenContext.class))).thenAnswer(invocation -> {
            OAuth2TokenContext context = invocation.getArgument(0);
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                scopes.set(context.getAuthorizedScopes());
                return Jwt.withTokenValue("access-token").header("alg", "RS256").subject("user-1")
                        .issuedAt(now).expiresAt(now.plusSeconds(3600))
                        .claim("scope", context.getAuthorizedScopes()).build();
            }
            return new OAuth2RefreshToken("refresh-token", now, now.plus(Duration.ofDays(365)));
        });
        NativeTokenIssuer issuer = new NativeTokenIssuer(clients, generator, mock(OAuth2AuthorizationService.class),
                AuthorizationServerSettings.builder().issuer("http://localhost:8085").build());
        UserPrincipal user = new UserPrincipal("user-1", "player@example.com", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        issuer.issue(UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities()), "mobile");

        com.fasterxml.jackson.databind.ObjectMapper mapper = OAuth2AuthorizationObjectMapper.create();
        java.util.Map<String, Object> metadata = java.util.Map.of("metadata.token.claims", new java.util.HashMap<>(java.util.Map.of("scope", scopes.get())));
        String json = mapper.writeValueAsString(new java.util.HashMap<>(metadata));
        java.util.Map<String, Object> back = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        assertThat(back).containsKey("metadata.token.claims");
    }
}
