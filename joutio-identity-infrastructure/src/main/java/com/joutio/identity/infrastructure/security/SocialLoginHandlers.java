package com.joutio.identity.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Fin du login social (Google, Facebook) : retour vers la page qui l'a lancé. Depuis la page des outils
 * ({@link HostedSignIn#SOCIAL_MARKER} en session), on reprend l'autorisation de l'outil ; sinon, retour dans l'app.
 */
public final class SocialLoginHandlers {

    private SocialLoginHandlers() {
    }

    public static AuthenticationSuccessHandler success(String appSuccessUri) {
        return (request, response, authentication) ->
                response.sendRedirect(fromHostedSignIn(request) ? HostedSignIn.CONTINUE_PATH : appSuccessUri);
    }

    public static AuthenticationFailureHandler failure(String appFailureUri) {
        return (request, response, exception) -> {
            String errorCode = "oauth2";
            if (exception instanceof OAuth2AuthenticationException oauth2Exception) {
                String providerError = oauth2Exception.getError().getErrorCode();
                if ("account_suspended".equals(providerError)) {
                    errorCode = "account_suspended";
                } else if (FacebookOAuth2UserService.EMAIL_MISSING_ERROR_CODE.equals(providerError)) {
                    errorCode = "facebook_email_missing";
                }
            }
            String base = fromHostedSignIn(request) ? HostedSignIn.PATH : appFailureUri;
            response.sendRedirect(UriComponentsBuilder.fromUriString(base).replaceQueryParam("error", errorCode).build().toUriString());
        };
    }

    /** Consomme la marque posée par {@link HostedSignIn#GOOGLE_PATH}. */
    private static boolean fromHostedSignIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(HostedSignIn.SOCIAL_MARKER) == null) {
            return false;
        }
        session.removeAttribute(HostedSignIn.SOCIAL_MARKER);
        return true;
    }
}
