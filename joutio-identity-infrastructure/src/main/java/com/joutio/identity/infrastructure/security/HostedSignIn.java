package com.joutio.identity.infrastructure.security;

/**
 * Connexion hébergée par identity pour les **outils techniques** (console, Grafana, ArgoCD, Swagger) : ils n'ont pas
 * le parcours joueur de l'app. Les joueurs (clients web et mobile) gardent la connexion de l'app.
 *
 * <p>La page appelle l'API web existante (code par email, Google) ; une fois la session identity ouverte, elle rejoint
 * {@link #CONTINUE_PATH}, qui reprend l'autorisation mémorisée par Spring Security au moment de la redirection.</p>
 */
public final class HostedSignIn {

    /** Page de connexion des outils. */
    public static final String PATH = "/sign-in";
    /** Reprise de l'autorisation OAuth2 mémorisée (requête sauvegardée en session). */
    public static final String CONTINUE_PATH = PATH + "/continue";
    /** Démarrage du login Google depuis la page des outils. */
    public static final String GOOGLE_PATH = PATH + "/google";
    /** Marque de session : le login social en cours vient de la page des outils, pas de l'app. */
    public static final String SOCIAL_MARKER = HostedSignIn.class.getName() + ".SOCIAL";

    private HostedSignIn() {
    }
}
