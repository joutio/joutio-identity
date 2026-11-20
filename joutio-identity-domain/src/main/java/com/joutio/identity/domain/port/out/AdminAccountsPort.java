package com.joutio.identity.domain.port.out;

/**
 * Comptes administrateurs ({@code ROLE_ADMIN}) : compte système et liste blanche d'adresses de la configuration.
 */
public interface AdminAccountsPort {
    boolean isAdmin(String userId, String email);
}
