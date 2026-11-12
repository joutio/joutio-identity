package com.joutio.identity.domain.exception;

import com.joutio.microservice.core.domain.exception.ProblemCategory;

/** Problèmes métier de l'attribution du droit Premium. */
public final class PremiumProblems {

    private PremiumProblems() {
    }

    public static class PremiumNotGrantableProblem extends UserReportProblems.BaseAccountProblem {
        public PremiumNotGrantableProblem(String userId) {
            super(userId, "urn:joutio:identity:premiumNotGrantable",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Premium not grantable",
                    "Premium cannot be granted to a deleted or system account");
        }
    }

    public static class InvalidPremiumGrantProblem extends UserReportProblems.BaseAccountProblem {
        public InvalidPremiumGrantProblem(String userId, String detail) {
            super(userId, "urn:joutio:identity:invalidPremiumGrant",
                    ProblemCategory.VALIDATION,
                    "Invalid premium grant",
                    detail);
        }
    }
}
