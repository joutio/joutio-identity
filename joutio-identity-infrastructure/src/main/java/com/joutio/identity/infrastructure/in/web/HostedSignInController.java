package com.joutio.identity.infrastructure.in.web;

import com.joutio.identity.infrastructure.security.HostedSignIn;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

/**
 * Connexion hébergée des outils techniques ({@link HostedSignIn}) : la page, la reprise de l'autorisation après la
 * connexion et le départ vers Google.
 */
@Controller
public class HostedSignInController {

    private static final Resource PAGE = new ClassPathResource("sign-in/index.html");

    /** Même cache que l'{@code ExceptionTranslationFilter} du serveur d'autorisation (session HTTP). */
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @GetMapping(HostedSignIn.PATH)
    public ResponseEntity<Resource> page() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .cacheControl(CacheControl.noStore())
                .body(PAGE);
    }

    /**
     * Après la connexion : l'autorisation de l'outil mémorisée en session, qui aboutit maintenant. Sans autorisation
     * en attente (page ouverte directement), retour sur la page, qui l'annonce.
     */
    @GetMapping(HostedSignIn.CONTINUE_PATH)
    public ResponseEntity<Void> resume(HttpServletRequest request, HttpServletResponse response) {
        SavedRequest saved = requestCache.getRequest(request, response);
        if (saved == null) {
            return redirect(HostedSignIn.PATH + "?signedIn");
        }
        requestCache.removeRequest(request, response);
        return redirect(saved.getRedirectUrl());
    }

    /** Login Google lancé depuis la page des outils : la fin du flux revient ici plutôt que dans l'app. */
    @GetMapping(HostedSignIn.GOOGLE_PATH)
    public ResponseEntity<Void> google(HttpServletRequest request) {
        request.getSession().setAttribute(HostedSignIn.SOCIAL_MARKER, Boolean.TRUE);
        return redirect("/oauth2/authorization/google");
    }

    private static ResponseEntity<Void> redirect(String target) {
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(target)).build();
    }
}
