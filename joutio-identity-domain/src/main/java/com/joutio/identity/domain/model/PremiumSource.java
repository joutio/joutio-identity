package com.joutio.identity.domain.model;

/**
 * Origine d'un droit Premium. Seul {@link #MANUAL} (attribution par un administrateur) est émis
 * avant l'ouverture des paiements ; les autres valeurs existent dès maintenant pour qu'aucun
 * consommateur ancien ne reçoive une constante inconnue lorsque {@code joutio-billing} arrivera.
 */
public enum PremiumSource {
    MANUAL,
    APP_STORE,
    PLAY,
    WEB
}
