package com.joutio.identity.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ClientLoginEntryPointTest {

    private final ClientLoginEntryPoint entryPoint =
            new ClientLoginEntryPoint("https://app.joutio.local/login", Set.of("grafana", "console"));

    @Test
    void sendsToolsToTheHostedSignInPage() throws Exception {
        assertThat(redirectFor("grafana")).isEqualTo("/sign-in");
        assertThat(redirectFor("console")).isEqualTo("/sign-in");
    }

    @Test
    void sendsPlayersToTheAppLogin() throws Exception {
        assertThat(redirectFor("web")).isEqualTo("https://app.joutio.local/login");
        assertThat(redirectFor(null)).isEqualTo("https://app.joutio.local/login");
    }

    private String redirectFor(String clientId) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorize");
        if (clientId != null) {
            request.setParameter("client_id", clientId);
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        entryPoint.commence(request, response, new BadCredentialsException("unauthorized"));
        return response.getRedirectedUrl();
    }
}
