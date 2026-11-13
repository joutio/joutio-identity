package com.joutio.identity.application.projection;

import com.joutio.identity.domain.event.UserEvent;
import com.joutio.identity.domain.port.out.ReferralPort;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Projection des rattachements filleul → parrain ; effacés avec le compte. */
@Component
@ProcessingGroup("referral-projection")
public class ReferralProjection {

    private static final Logger logger = LoggerFactory.getLogger(ReferralProjection.class);

    private final ReferralPort referrals;

    public ReferralProjection(ReferralPort referrals) {
        this.referrals = referrals;
    }

    @EventHandler
    @Transactional
    public void on(UserEvent.UserReferredEvent event) {
        referrals.saveReferral(event.userId(), event.referrerId(), event.referredAt());
        logger.info("Referral projected: userId={}, referrerId={}", event.userId(), event.referrerId());
    }

    @EventHandler
    @Transactional
    public void on(UserEvent.UserAccountDeletedEvent event) {
        referrals.erase(event.userId());
    }
}
