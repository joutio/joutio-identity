package com.joutio.identity.domain.model;

/**
 * Update de {@code WatchUsersQuery} : le compte après une écriture (un compte supprimé reste, anonymisé, au statut
 * {@code DELETED}). {@code version} est monotone par compte.
 */
public record UserUpdate(String userId, long version, User user) {
}
