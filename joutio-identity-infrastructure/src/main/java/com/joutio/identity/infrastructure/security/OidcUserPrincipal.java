package com.joutio.identity.infrastructure.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import com.joutio.microservice.core.domain.model.security.JoutioPrincipal;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Principal utilisateur pour OIDC (OpenID Connect)
 * Utilisé pour les logins Google, etc.
 * Serializable : le SecurityContext est persisté dans la session Spring Session JDBC.
 */
public class OidcUserPrincipal implements OidcUser, JoutioPrincipal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final String userId;

    @Getter
    private final String email;

    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public OidcUserPrincipal(String userId, String email, OidcIdToken idToken, OidcUserInfo userInfo,
                             Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.idToken = idToken;
        this.userInfo = userInfo;
        this.attributes = attributes;
        this.authorities = authorities != null ? authorities : java.util.List.of();
    }

    @Override
    public Map<String, Object> getClaims() {
        return idToken.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return userId;
    }
}
