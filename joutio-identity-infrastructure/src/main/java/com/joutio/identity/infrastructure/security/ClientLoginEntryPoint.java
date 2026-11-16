package com.joutio.identity.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import java.io.IOException;
import java.util.Set;

/**
 * Entry point OIDC : choisit la page de connexion selon le client qui demande l'autorisation.
 *
 * <ul>
 *   <li>Outil technique ({@code app.security.tool-clients}) : page hébergée par identity ({@link HostedSignIn#PATH}).
 *   Spring Security a déjà mémorisé l'autorisation en session ; la page la reprend après la connexion.</li>
 *   <li>Joueur (web, mobile) : connexion de l'app ({@code app.security.login-page-uri}), qui enchaîne son propre PKCE.</li>
 * </ul>
 */
public class ClientLoginEntryPoint implements AuthenticationEntryPoint {

    private final String appLoginPageUri;
    private final Set<String> toolClients;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public ClientLoginEntryPoint(String appLoginPageUri, Set<String> toolClients) {
        this.appLoginPageUri = appLoginPageUri;
        this.toolClients = Set.copyOf(toolClients);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
        String target = clientId != null && toolClients.contains(clientId) ? HostedSignIn.PATH : appLoginPageUri;
        redirectStrategy.sendRedirect(request, response, target);
    }
}
