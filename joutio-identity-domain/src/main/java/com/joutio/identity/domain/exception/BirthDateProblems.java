package com.joutio.identity.domain.exception;

import com.joutio.microservice.core.domain.exception.ProblemCategory;
import com.joutio.identity.domain.model.BirthDateRules;

import java.util.Map;

/** Problèmes métier de la date de naissance. */
public final class BirthDateProblems {

    private BirthDateProblems() {
    }

    public static class InvalidBirthDateProblem extends UserReportProblems.BaseAccountProblem {
        public InvalidBirthDateProblem(String userId, String detail) {
            super(userId, "urn:joutio:identity:invalidBirthDate",
                    ProblemCategory.VALIDATION,
                    "Invalid birth date",
                    detail);
        }
    }

    public static class UnderageProblem extends UserReportProblems.BaseAccountProblem {
        public UnderageProblem(String userId) {
            super(userId, "urn:joutio:identity:underage",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Underage",
                    "Joutio is restricted to players aged " + BirthDateRules.MIN_AGE + " and over",
                    Map.<String, Object>of("minAge", BirthDateRules.MIN_AGE));
        }
    }

    public static class BirthDateNotEditableProblem extends UserReportProblems.BaseAccountProblem {
        public BirthDateNotEditableProblem(String userId) {
            super(userId, "urn:joutio:identity:birthDateNotEditable",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Birth date not editable",
                    "The birth date of a deleted or system account cannot be set");
        }
    }

    /** Acceptation des CGU impossible : compte supprimé ou système, ou version vide. */
    public static class TermsNotAcceptableProblem extends UserReportProblems.BaseAccountProblem {
        public TermsNotAcceptableProblem(String userId) {
            super(userId, "urn:joutio:identity:termsNotAcceptable",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Terms not acceptable",
                    "The terms cannot be accepted for this account or this version");
        }
    }
}
