package com.joutio.identity.application.handler.query;

import com.joutio.identity.domain.model.ReferralRules;
import com.joutio.identity.domain.model.ReferralSummary;
import com.joutio.identity.domain.port.out.ReferralPort;
import com.joutio.identity.domain.query.UserQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class ReferralQueryHandler {

    private final ReferralPort referrals;

    public ReferralQueryHandler(ReferralPort referrals) {
        this.referrals = referrals;
    }

    @QueryHandler
    public ReferralSummary handle(UserQuery.GetReferralSummaryQuery query) {
        return new ReferralSummary(
                query.userId(),
                referrals.codeFor(query.userId()),
                referrals.countReferrals(query.userId()),
                referrals.referrerOf(query.userId()).orElse(null));
    }

    @QueryHandler
    public String handle(UserQuery.FindUserIdByReferralCodeQuery query) {
        String code = ReferralRules.normalizeCode(query.code());
        return code == null ? "" : referrals.userIdForCode(code).orElse("");
    }
}
