package com.joutio.identity.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import static org.assertj.core.api.Assertions.assertThat;

class SocialLoginHandlersTest {

    @Test
    void successReturnsToTheAppByDefault() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        SocialLoginHandlers.success("https://app/login?social=success")
                .onAuthenticationSuccess(request, response, new TestingAuthenticationToken("u", null));
        assertThat(response.getRedirectedUrl()).isEqualTo("https://app/login?social=success");
    }

    @Test
    void successFromTheToolsPageResumesTheAuthorizationOnce() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(HostedSignIn.SOCIAL_MARKER, Boolean.TRUE);
        MockHttpServletResponse response = new MockHttpServletResponse();
        SocialLoginHandlers.success("https://app/login?social=success")
                .onAuthenticationSuccess(request, response, new TestingAuthenticationToken("u", null));
        assertThat(response.getRedirectedUrl()).isEqualTo("/sign-in/continue");
        assertThat(request.getSession().getAttribute(HostedSignIn.SOCIAL_MARKER)).isNull();
    }

    @Test
    void failureFromTheToolsPageStaysOnIt() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(HostedSignIn.SOCIAL_MARKER, Boolean.TRUE);
        MockHttpServletResponse response = new MockHttpServletResponse();
        SocialLoginHandlers.failure("https://app/login?error")
                .onAuthenticationFailure(request, response, new OAuth2AuthenticationException(new OAuth2Error("account_suspended")));
        assertThat(response.getRedirectedUrl()).isEqualTo("/sign-in?error=account_suspended");
    }

    @Test
    void failureReturnsToTheAppByDefault() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        SocialLoginHandlers.failure("https://app/login?error")
                .onAuthenticationFailure(request, response, new OAuth2AuthenticationException(new OAuth2Error("x")));
        assertThat(response.getRedirectedUrl()).isEqualTo("https://app/login?error=oauth2");
    }
}
