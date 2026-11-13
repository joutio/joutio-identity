package com.joutio.identity.domain.exception;

import com.joutio.microservice.core.domain.exception.ProblemCategory;

/** Problèmes métier du parrainage. */
public final class ReferralProblems {

    private ReferralProblems() {
    }

    public static class InvalidReferralCodeProblem extends UserReportProblems.BaseAccountProblem {
        public InvalidReferralCodeProblem(String userId) {
            super(userId, "urn:joutio:identity:invalidReferralCode",
                    ProblemCategory.VALIDATION,
                    "Invalid referral code",
                    "The referral code is unknown or belongs to an unavailable account");
        }
    }

    public static class SelfReferralProblem extends UserReportProblems.BaseAccountProblem {
        public SelfReferralProblem(String userId) {
            super(userId, "urn:joutio:identity:selfReferral",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Self referral",
                    "A player cannot use their own referral code");
        }
    }

    public static class AlreadyReferredProblem extends UserReportProblems.BaseAccountProblem {
        public AlreadyReferredProblem(String userId) {
            super(userId, "urn:joutio:identity:alreadyReferred",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Already referred",
                    "A referral code has already been applied to this account");
        }
    }

    public static class ReferralWindowClosedProblem extends UserReportProblems.BaseAccountProblem {
        public ReferralWindowClosedProblem(String userId) {
            super(userId, "urn:joutio:identity:referralWindowClosed",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Referral window closed",
                    "A referral code can only be applied within 30 days of registration");
        }
    }
}
