package com.joutio.identity.domain.port.out;

/**
 * Port sortant - version du compte surveillé par les caches du BFF : un compteur par compte, incrémenté atomiquement à
 * chaque écriture, donc monotone même si plusieurs répliques écrivent pour le même compte.
 */
public interface LiveVersionPort {

    /** Incrémente la version du compte et renvoie la nouvelle valeur (1 à la première écriture). */
    long increment(String userId);
}
