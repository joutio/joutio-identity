package com.joutio.identity.domain.exception;

import com.joutio.microservice.core.domain.exception.ProblemCategory;

import java.util.Map;


public interface UserProblems {

    class UserNotFoundProblem extends UserProblem {
        public UserNotFoundProblem(String userId) {
            super(userId, "urn:joutio:user:notFound",
                    ProblemCategory.BUSINESS_RESOURCE_MISSING,
                    "User not found",
                    "The user " + userId + " was not found", null);
        }
    }

    class InvalidLoginCodeProblem extends UserProblem {
        public InvalidLoginCodeProblem(String email) {
            super("UNKNOWN", "urn:joutio:auth:invalidLoginCode", "Invalid login code",
                    "The login code is invalid, expired or already used",
                    Map.of("email", email));
        }
    }

    class InvalidAccountDeletionCodeProblem extends UserProblem {
        public InvalidAccountDeletionCodeProblem(String email) {
            super("UNKNOWN", "urn:joutio:account:invalidDeletionCode", "Invalid deletion code",
                    "The account deletion code is invalid, expired or already used",
                    Map.of("email", email));
        }
    }

    class InvalidEmailFormatProblem extends UserProblem {
        public InvalidEmailFormatProblem(String userId, String email) {
            super(userId, "urn:joutio:user:invalidEmailFormat", "Invalid email format",
                    "The email " + email + " has an invalid format",
                    Map.of("email", email));
        }
    }

    class SocialProviderMissingProblem extends UserProblem {
        public SocialProviderMissingProblem(String userId) {
            super(userId, "urn:joutio:user:missingSocialProvider",
                    "Missing social provider",
                    "Provider information is required for social registration");
        }
    }

    class UserAlreadyExistsProblem extends UserProblem {
        public UserAlreadyExistsProblem(String userId, String email) {
            super(userId, "urn:joutio:user:alreadyExists", "User already exists",
                    "A user with email " + email + " already exists");
        }
    }
}
