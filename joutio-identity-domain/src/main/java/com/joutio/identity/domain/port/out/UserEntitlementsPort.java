package com.joutio.identity.domain.port.out;

import com.joutio.identity.domain.model.UserEntitlements;

public interface UserEntitlementsPort {
    UserEntitlements get(String userId);
}
