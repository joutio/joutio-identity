package com.joutio.identity.domain.aggregate;

import com.joutio.axon.test.JoutioAxonMatchers;
import com.joutio.identity.domain.command.UserCommand;
import com.joutio.identity.domain.event.UserEvent;
import com.joutio.identity.domain.exception.UserProblems;
import com.joutio.identity.domain.exception.PremiumProblems;
import com.joutio.identity.domain.model.PremiumSource;
import com.joutio.identity.domain.model.SocialProvider;
import com.joutio.identity.domain.model.SuspensionReinstatementReason;
import com.joutio.identity.domain.model.UserSuspensionReason;
import com.joutio.identity.domain.port.out.EmailClaimPort;
import com.joutio.microservice.core.domain.constant.JoutioConstants;
import com.joutio.identity.domain.event.UserEvent.UserAccountDeletedEvent;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Axon in-memory de l'agrégat {@link UserAggregate} via {@link AggregateTestFixture}.
 * <p>
 * 100 % in-memory : event store de l'agrégat en mémoire, aucun Postgres ni Axon Server.
 * L'unicité d'email s'appuie désormais sur {@link EmailClaimPort} (réservation atomique).
 */
class UserAggregateTest {

    private final AggregateTestFixture<UserAggregate> fixture =
            new AggregateTestFixture<>(UserAggregate.class);

    private EmailClaimPort claimPort(boolean granted) {
        EmailClaimPort port = mock(EmailClaimPort.class);
        when(port.claim(anyString(), anyString())).thenReturn(granted);
        return port;
    }

    @Test
    void registerUser_appliesUserRegisteredEventWithoutProvider() {
        fixture.registerInjectableResource(claimPort(true))
                .givenNoPriorActivity()
                .when(new UserCommand.RegisterUserCommand("user-1", "user@joutio.dev"))
                .expectEventsMatching(JoutioAxonMatchers.singlePayloadMatching(
                        UserEvent.UserRegisteredEvent.class,
                        e -> {
                            UserEvent.UserRegisteredEvent event = (UserEvent.UserRegisteredEvent) e;
                            return event.userId().equals("user-1")
                                    && event.email().equals("user@joutio.dev")
                                    && event.provider() == null
                                    && event.createdAt() != null;
                        }));
    }

    @Test
    void registerWithSocial_appliesUserRegisteredEventWithProvider() {
        fixture.registerInjectableResource(claimPort(true))
                .givenNoPriorActivity()
                .when(new UserCommand.RegisterUserWithSocialCommand("user-2", "user@joutio.dev", SocialProvider.GOOGLE))
                .expectEventsMatching(JoutioAxonMatchers.singlePayloadMatching(
                        UserEvent.UserRegisteredEvent.class,
                        e -> {
                            UserEvent.UserRegisteredEvent event = (UserEvent.UserRegisteredEvent) e;
                            return event.userId().equals("user-2")
                                    && event.email().equals("user@joutio.dev")
                                    && event.provider() == SocialProvider.GOOGLE
                                    && event.createdAt() != null;
                        }));
    }

    @Test
    void registerWithInvalidEmail_throwsInvalidEmailFormat() {
        fixture.registerInjectableResource(claimPort(true))
                .givenNoPriorActivity()
                .when(new UserCommand.RegisterUserCommand("user-3", "invalid-email"))
                .expectException(UserProblems.InvalidEmailFormatProblem.class);
    }

    @Test
    void registerWithAlreadyClaimedEmail_throwsUserAlreadyExists() {
        fixture.registerInjectableResource(claimPort(false))
                .givenNoPriorActivity()
                .when(new UserCommand.RegisterUserCommand("user-6", "taken@joutio.dev"))
                .expectException(UserProblems.UserAlreadyExistsProblem.class);
    }

    @Test
    void linkSocialProvider_appliesProviderLinkedEvent() {
        fixture.given(new UserEvent.UserRegisteredEvent(
                        "user-4", "user@joutio.dev", null, Instant.parse("2026-01-01T00:00:00Z")))
                .when(new UserCommand.LinkSocialProviderCommand("user-4", SocialProvider.GOOGLE))
                .expectEventsMatching(JoutioAxonMatchers.singlePayloadMatching(
                        UserEvent.SocialProviderLinkedEvent.class,
                        e -> {
                            UserEvent.SocialProviderLinkedEvent event = (UserEvent.SocialProviderLinkedEvent) e;
                            return event.userId().equals("user-4")
                                    && event.provider() == SocialProvider.GOOGLE
                                    && event.linkedAt() != null;
                        }));
    }

    @Test
    void linkSocialProvider_whenAlreadyLinked_emitsNoEvent() {
        fixture.given(new UserEvent.UserRegisteredEvent(
                        "user-5", "user@joutio.dev", SocialProvider.GOOGLE, Instant.parse("2026-01-01T00:00:00Z")))
                .when(new UserCommand.LinkSocialProviderCommand("user-5", SocialProvider.GOOGLE))
                .expectNoEvents();
    }

    @Test
    void recordLogin_appliesUserLoggedInEvent() {
        fixture.given(new UserEvent.UserRegisteredEvent(
                        "user-7", "user@joutio.dev", null, Instant.parse("2026-01-01T00:00:00Z")))
                .when(new UserCommand.RecordUserLoginCommand("user-7"))
                .expectEventsMatching(JoutioAxonMatchers.singlePayloadMatching(
                        UserEvent.UserLoggedInEvent.class,
                        e -> {
                            UserEvent.UserLoggedInEvent event = (UserEvent.UserLoggedInEvent) e;
                            return event.userId().equals("user-7") && event.loggedInAt() != null;
                        }));
    }

    @Test
    void recordLogin_onUnknownUser_throwsAggregateNotFound() {
        fixture.givenNoPriorActivity()
                .when(new UserCommand.RecordUserLoginCommand("unknown-user"))
                .expectException(AggregateNotFoundException.class);
    }

    @Test
    void deleteAccount_releasesEmailAndEmitsTransverseEvent() {
        EmailClaimPort port = claimPort(true);
        fixture.registerInjectableResource(port)
                .given(new UserEvent.UserRegisteredEvent(
                        "user-9", "delete@joutio.dev", null,
                        Instant.parse("2026-01-01T00:00:00Z")))
                .when(new UserCommand.DeleteAccountCommand(
                        "user-9", "deletion-1", Instant.parse("2026-10-22T10:00:00Z")))
                .expectEventsMatching(JoutioAxonMatchers.singlePayloadMatching(
                        UserAccountDeletedEvent.class,
                        e -> {
                            UserAccountDeletedEvent event = (UserAccountDeletedEvent) e;
                            return event.userId().equals("user-9")
                                    && event.deletionId().equals("deletion-1")
                                    && event.deletedAt().equals(Instant.parse("2026-10-22T10:00:00Z"));
                        }));
        verify(port).release("delete@joutio.dev", "user-9");
    }

    @Test
    void suspend_appliesSuspensionEvent() {
        Instant until = Instant.parse("2026-02-01T00:00:00Z");
        fixture.given(registered())
                .when(new UserCommand.SuspendUserCommand(
                        "user-8", UserSuspensionReason.HARASSMENT, "internal note",
                        "admin-1", until, Instant.parse("2026-01-01T00:00:00Z")))
                .expectEventsMatching(JoutioAxonMatchers.singlePayloadMatching(
                        UserEvent.UserSuspendedEvent.class,
                        e -> {
                            UserEvent.UserSuspendedEvent event = (UserEvent.UserSuspendedEvent) e;
                            return event.userId().equals("user-8")
                                    && event.email().equals("user@joutio.dev")
                                    && event.suspensionId() != null
                                    && event.reason() == UserSuspensionReason.HARASSMENT
                                    && event.note().equals("internal note")
                                    && event.suspendedBy().equals("admin-1")
                                    && event.suspendedUntil().equals(until);
                        }));
    }

    @Test
    void suspend_withoutEnd_isPermanent() {
        fixture.given(registered())
                .when(new UserCommand.SuspendUserCommand(
                        "user-8", UserSuspensionReason.SPAM, null,
                        "admin-1", null, Instant.parse("2026-01-01T00:00:00Z")))
                .expectEventsMatching(JoutioAxonMatchers.singlePayloadMatching(
                        UserEvent.UserSuspendedEvent.class,
                        e -> ((UserEvent.UserSuspendedEvent) e).suspendedUntil() == null));
    }

    @Test
    void suspend_systemAccount_isRejected() {
        fixture.given(new UserEvent.UserRegisteredEvent(
                        JoutioConstants.SYSTEM_USER_ID, JoutioConstants.SYSTEM_USER_EMAIL, null,
                        Instant.parse("2026-01-01T00:00:00Z")))
                .when(new UserCommand.SuspendUserCommand(
                        JoutioConstants.SYSTEM_USER_ID, UserSuspensionReason.OTHER, null,
                        "admin-1", null, Instant.parse("2026-01-01T00:00:00Z")))
                .expectException(com.joutio.identity.domain.exception.UserReportProblems
                        .SystemAccountNotSuspendableProblem.class);
    }

    @Test
    void suspend_alreadySuspended_isRejected() {
        fixture.given(registered(), suspended(Instant.parse("2026-02-01T00:00:00Z")))
                .when(new UserCommand.SuspendUserCommand(
                        "user-8", UserSuspensionReason.SPAM, null,
                        "admin-1", null, Instant.parse("2026-01-15T00:00:00Z")))
                .expectException(com.joutio.identity.domain.exception.UserReportProblems
                        .AlreadySuspendedProblem.class);
    }

    @Test
    void suspend_afterExpiry_isAllowed() {
        fixture.given(registered(), suspended(Instant.parse("2026-01-10T00:00:00Z")))
                .when(new UserCommand.SuspendUserCommand(
                        "user-8", UserSuspensionReason.SPAM, null,
                        "admin-1", Instant.parse("2026-02-01T00:00:00Z"),
                        Instant.parse("2026-01-15T00:00:00Z")))
                .expectEventsMatching(JoutioAxonMatchers.singlePayloadMatching(
                        UserEvent.UserSuspendedEvent.class,
                        e -> ((UserEvent.UserSuspendedEvent) e).suspendedUntil()
                                .equals(Instant.parse("2026-02-01T00:00:00Z"))));
    }

    @Test
    void reinstate_suspendedAccount_appliesEvent() {
        fixture.given(registered(), suspended(null))
                .when(new UserCommand.ReinstateUserCommand(
                        "user-8", SuspensionReinstatementReason.MANUAL, "appeal accepted",
                        "admin-1", Instant.parse("2026-01-20T00:00:00Z")))
                .expectEventsMatching(JoutioAxonMatchers.singlePayloadMatching(
                        UserEvent.UserReinstatedEvent.class,
                        e -> {
                            UserEvent.UserReinstatedEvent event = (UserEvent.UserReinstatedEvent) e;
                            return event.userId().equals("user-8")
                                    && event.reason() == SuspensionReinstatementReason.MANUAL
                                    && event.note().equals("appeal accepted")
                                    && event.reinstatedBy().equals("admin-1")
                                    && event.reinstatedAt().equals(Instant.parse("2026-01-20T00:00:00Z"));
                        }));
    }

    @Test
    void reinstate_activeAccount_emitsNoEvent() {
        fixture.given(registered())
                .when(new UserCommand.ReinstateUserCommand(
                        "user-8", SuspensionReinstatementReason.MANUAL, null,
                        "admin-1", Instant.parse("2026-01-20T00:00:00Z")))
                .expectNoEvents();
    }

    private static final Instant GRANTED_AT = Instant.parse("2026-01-10T00:00:00Z");
    private static final Instant PREMIUM_UNTIL = Instant.parse("2026-02-10T00:00:00Z");

    @Test
    void grantPremium_appliesGrantedEvent() {
        fixture.given(registered())
                .when(new UserCommand.GrantPremiumCommand(
                        "user-8", PremiumSource.MANUAL, PREMIUM_UNTIL, "admin-1", GRANTED_AT))
                .expectEvents(new UserEvent.UserPremiumGrantedEvent(
                        "user-8", PremiumSource.MANUAL, PREMIUM_UNTIL, "admin-1", GRANTED_AT));
    }

    @Test
    void grantPremium_withoutEnd_isPermanent() {
        fixture.given(registered())
                .when(new UserCommand.GrantPremiumCommand(
                        "user-8", PremiumSource.MANUAL, null, "admin-1", GRANTED_AT))
                .expectEvents(new UserEvent.UserPremiumGrantedEvent(
                        "user-8", PremiumSource.MANUAL, null, "admin-1", GRANTED_AT));
    }

    @Test
    void grantPremium_sameActiveGrant_isIdempotent() {
        fixture.given(registered(), premiumGranted(PREMIUM_UNTIL))
                .when(new UserCommand.GrantPremiumCommand(
                        "user-8", PremiumSource.MANUAL, PREMIUM_UNTIL, "admin-2",
                        Instant.parse("2026-01-12T00:00:00Z")))
                .expectNoEvents();
    }

    @Test
    void grantPremium_extension_replacesEnd() {
        Instant extended = Instant.parse("2026-03-10T00:00:00Z");
        fixture.given(registered(), premiumGranted(PREMIUM_UNTIL))
                .when(new UserCommand.GrantPremiumCommand(
                        "user-8", PremiumSource.MANUAL, extended, "admin-1",
                        Instant.parse("2026-01-12T00:00:00Z")))
                .expectEvents(new UserEvent.UserPremiumGrantedEvent(
                        "user-8", PremiumSource.MANUAL, extended, "admin-1",
                        Instant.parse("2026-01-12T00:00:00Z")));
    }

    @Test
    void grantPremium_afterExpiry_grantsAgain() {
        Instant later = Instant.parse("2026-02-20T00:00:00Z");
        fixture.given(registered(), premiumGranted(PREMIUM_UNTIL))
                .when(new UserCommand.GrantPremiumCommand(
                        "user-8", PremiumSource.MANUAL, PREMIUM_UNTIL.plusSeconds(86_400 * 30), "admin-1", later))
                .expectEventsMatching(JoutioAxonMatchers.singlePayloadMatching(
                        UserEvent.UserPremiumGrantedEvent.class,
                        e -> ((UserEvent.UserPremiumGrantedEvent) e).grantedAt().equals(later)));
    }

    @Test
    void grantPremium_pastEnd_isRejected() {
        fixture.given(registered())
                .when(new UserCommand.GrantPremiumCommand(
                        "user-8", PremiumSource.MANUAL, GRANTED_AT, "admin-1", GRANTED_AT))
                .expectException(PremiumProblems.InvalidPremiumGrantProblem.class);
    }

    @Test
    void grantPremium_withoutSource_isRejected() {
        fixture.given(registered())
                .when(new UserCommand.GrantPremiumCommand(
                        "user-8", null, PREMIUM_UNTIL, "admin-1", GRANTED_AT))
                .expectException(PremiumProblems.InvalidPremiumGrantProblem.class);
    }

    @Test
    void grantPremium_deletedAccount_isRejected() {
        fixture.given(registered(), new UserAccountDeletedEvent("user-8", "deletion-1", GRANTED_AT))
                .when(new UserCommand.GrantPremiumCommand(
                        "user-8", PremiumSource.MANUAL, null, "admin-1", GRANTED_AT))
                .expectException(PremiumProblems.PremiumNotGrantableProblem.class);
    }

    @Test
    void grantPremium_systemAccount_isRejected() {
        fixture.given(new UserEvent.UserRegisteredEvent(
                        JoutioConstants.SYSTEM_USER_ID, JoutioConstants.SYSTEM_USER_EMAIL, null,
                        Instant.parse("2026-01-01T00:00:00Z")))
                .when(new UserCommand.GrantPremiumCommand(
                        JoutioConstants.SYSTEM_USER_ID, PremiumSource.MANUAL, null, "admin-1", GRANTED_AT))
                .expectException(PremiumProblems.PremiumNotGrantableProblem.class);
    }

    @Test
    void grantPremium_suspendedAccount_isStoredButNotEffective() {
        // La suspension neutralise le droit à la lecture (PremiumRules.isEffective), pas à l'écriture.
        fixture.given(registered(), suspended(null))
                .when(new UserCommand.GrantPremiumCommand(
                        "user-8", PremiumSource.MANUAL, PREMIUM_UNTIL, "admin-1", GRANTED_AT))
                .expectEvents(new UserEvent.UserPremiumGrantedEvent(
                        "user-8", PremiumSource.MANUAL, PREMIUM_UNTIL, "admin-1", GRANTED_AT));
    }

    @Test
    void revokePremium_activeGrant_appliesRevokedEvent() {
        Instant revokedAt = Instant.parse("2026-01-15T00:00:00Z");
        fixture.given(registered(), premiumGranted(PREMIUM_UNTIL))
                .when(new UserCommand.RevokePremiumCommand("user-8", "test ended", "admin-1", revokedAt))
                .expectEvents(new UserEvent.UserPremiumRevokedEvent(
                        "user-8", PremiumSource.MANUAL, "test ended", "admin-1", revokedAt));
    }

    @Test
    void revokePremium_withoutGrant_isNoOp() {
        fixture.given(registered())
                .when(new UserCommand.RevokePremiumCommand("user-8", null, "admin-1", GRANTED_AT))
                .expectNoEvents();
    }

    @Test
    void revokePremium_expiredGrant_isNoOp() {
        fixture.given(registered(), premiumGranted(PREMIUM_UNTIL))
                .when(new UserCommand.RevokePremiumCommand(
                        "user-8", null, "admin-1", Instant.parse("2026-03-01T00:00:00Z")))
                .expectNoEvents();
    }

    @Test
    void revokePremium_twice_secondIsNoOp() {
        Instant revokedAt = Instant.parse("2026-01-15T00:00:00Z");
        fixture.given(registered(), premiumGranted(PREMIUM_UNTIL),
                        new UserEvent.UserPremiumRevokedEvent("user-8", PremiumSource.MANUAL, null, "admin-1", revokedAt))
                .when(new UserCommand.RevokePremiumCommand("user-8", null, "admin-1", revokedAt.plusSeconds(60)))
                .expectNoEvents();
    }

    private static UserEvent.UserPremiumGrantedEvent premiumGranted(Instant until) {
        return new UserEvent.UserPremiumGrantedEvent("user-8", PremiumSource.MANUAL, until, "admin-1", GRANTED_AT);
    }

    private static UserEvent.UserRegisteredEvent registered() {
        return new UserEvent.UserRegisteredEvent(
                "user-8", "user@joutio.dev", null, Instant.parse("2026-01-01T00:00:00Z"));
    }

    private static UserEvent.UserSuspendedEvent suspended(Instant until) {
        return new UserEvent.UserSuspendedEvent(
                "user-8", "user@joutio.dev", "suspension-1",
                UserSuspensionReason.HARASSMENT, null, "admin-0",
                Instant.parse("2026-01-05T00:00:00Z"), until);
    }

    // ─── Date de naissance ───

    private static final Instant BIRTH_REQUESTED_AT = Instant.parse("2026-11-13T10:00:00Z");

    @Test
    void setBirthDate_fifteenToday_appliesEvent() {
        java.time.LocalDate birthDate = java.time.LocalDate.parse("2011-11-13");
        fixture.given(registered())
                .when(new UserCommand.SetBirthDateCommand("user-8", birthDate, "user-8", BIRTH_REQUESTED_AT))
                .expectEvents(new UserEvent.UserBirthDateSetEvent("user-8", birthDate, "user-8", BIRTH_REQUESTED_AT));
    }

    @Test
    void setBirthDate_fifteenTomorrow_isUnderage() {
        fixture.given(registered())
                .when(new UserCommand.SetBirthDateCommand(
                        "user-8", java.time.LocalDate.parse("2011-11-14"), "user-8", BIRTH_REQUESTED_AT))
                .expectException(com.joutio.identity.domain.exception.BirthDateProblems.UnderageProblem.class);
    }

    @Test
    void setBirthDate_leapDay_isFifteenOnFebruary28() {
        java.time.LocalDate leapDay = java.time.LocalDate.parse("2012-02-29");
        Instant feb28 = Instant.parse("2027-02-28T00:00:00Z");
        fixture.given(registered())
                .when(new UserCommand.SetBirthDateCommand("user-8", leapDay, "user-8", feb28))
                .expectEvents(new UserEvent.UserBirthDateSetEvent("user-8", leapDay, "user-8", feb28));
    }

    @Test
    void setBirthDate_inFuture_isInvalid() {
        fixture.given(registered())
                .when(new UserCommand.SetBirthDateCommand(
                        "user-8", java.time.LocalDate.parse("2027-01-01"), "user-8", BIRTH_REQUESTED_AT))
                .expectException(com.joutio.identity.domain.exception.BirthDateProblems.InvalidBirthDateProblem.class);
    }

    @Test
    void setBirthDate_overMaxAge_isInvalid() {
        fixture.given(registered())
                .when(new UserCommand.SetBirthDateCommand(
                        "user-8", java.time.LocalDate.parse("1900-01-01"), "user-8", BIRTH_REQUESTED_AT))
                .expectException(com.joutio.identity.domain.exception.BirthDateProblems.InvalidBirthDateProblem.class);
    }

    @Test
    void setBirthDate_missing_isInvalid() {
        fixture.given(registered())
                .when(new UserCommand.SetBirthDateCommand("user-8", null, "user-8", BIRTH_REQUESTED_AT))
                .expectException(com.joutio.identity.domain.exception.BirthDateProblems.InvalidBirthDateProblem.class);
    }

    @Test
    void setBirthDate_sameDate_isIdempotent() {
        java.time.LocalDate birthDate = java.time.LocalDate.parse("1998-03-04");
        fixture.given(registered(), new UserEvent.UserBirthDateSetEvent("user-8", birthDate, "user-8", BIRTH_REQUESTED_AT))
                .when(new UserCommand.SetBirthDateCommand("user-8", birthDate, "user-8", BIRTH_REQUESTED_AT))
                .expectNoEvents();
    }

    @Test
    void setBirthDate_correction_appliesNewEvent() {
        java.time.LocalDate first = java.time.LocalDate.parse("1998-03-04");
        java.time.LocalDate corrected = java.time.LocalDate.parse("1998-04-03");
        fixture.given(registered(), new UserEvent.UserBirthDateSetEvent("user-8", first, "user-8", BIRTH_REQUESTED_AT))
                .when(new UserCommand.SetBirthDateCommand("user-8", corrected, "user-8", BIRTH_REQUESTED_AT))
                .expectEvents(new UserEvent.UserBirthDateSetEvent("user-8", corrected, "user-8", BIRTH_REQUESTED_AT));
    }

    @Test
    void setBirthDate_deletedAccount_isRefused() {
        fixture.given(registered(), new UserAccountDeletedEvent("user-8", "deletion-1", BIRTH_REQUESTED_AT))
                .when(new UserCommand.SetBirthDateCommand(
                        "user-8", java.time.LocalDate.parse("1998-03-04"), "user-8", BIRTH_REQUESTED_AT))
                .expectException(com.joutio.identity.domain.exception.BirthDateProblems.BirthDateNotEditableProblem.class);
    }

    // ─── Parrainage ───

    @Test
    void applyReferral_withinWindow_appliesEvent() {
        Instant at = REGISTERED_AT_FOR_REFERRAL.plusSeconds(3600);
        fixture.given(registeredAt(REGISTERED_AT_FOR_REFERRAL))
                .when(new UserCommand.ApplyReferralCommand("user-8", "sponsor-1", at))
                .expectEvents(new UserEvent.UserReferredEvent("user-8", "sponsor-1", at));
    }

    @Test
    void applyReferral_self_isRefused() {
        fixture.given(registeredAt(REGISTERED_AT_FOR_REFERRAL))
                .when(new UserCommand.ApplyReferralCommand("user-8", "user-8", REGISTERED_AT_FOR_REFERRAL))
                .expectException(com.joutio.identity.domain.exception.ReferralProblems.SelfReferralProblem.class);
    }

    @Test
    void applyReferral_afterThirtyDays_isRefused() {
        fixture.given(registeredAt(REGISTERED_AT_FOR_REFERRAL))
                .when(new UserCommand.ApplyReferralCommand("user-8", "sponsor-1",
                        REGISTERED_AT_FOR_REFERRAL.plus(java.time.Duration.ofDays(31))))
                .expectException(com.joutio.identity.domain.exception.ReferralProblems.ReferralWindowClosedProblem.class);
    }

    @Test
    void applyReferral_sameSponsorAgain_isIdempotent() {
        fixture.given(registeredAt(REGISTERED_AT_FOR_REFERRAL),
                        new UserEvent.UserReferredEvent("user-8", "sponsor-1", REGISTERED_AT_FOR_REFERRAL))
                .when(new UserCommand.ApplyReferralCommand("user-8", "sponsor-1", REGISTERED_AT_FOR_REFERRAL))
                .expectNoEvents();
    }

    @Test
    void applyReferral_otherSponsor_isRefused() {
        fixture.given(registeredAt(REGISTERED_AT_FOR_REFERRAL),
                        new UserEvent.UserReferredEvent("user-8", "sponsor-1", REGISTERED_AT_FOR_REFERRAL))
                .when(new UserCommand.ApplyReferralCommand("user-8", "sponsor-2", REGISTERED_AT_FOR_REFERRAL))
                .expectException(com.joutio.identity.domain.exception.ReferralProblems.AlreadyReferredProblem.class);
    }

    @Test
    void applyReferral_systemSponsor_isRefused() {
        fixture.given(registeredAt(REGISTERED_AT_FOR_REFERRAL))
                .when(new UserCommand.ApplyReferralCommand("user-8", JoutioConstants.SYSTEM_USER_ID, REGISTERED_AT_FOR_REFERRAL))
                .expectException(com.joutio.identity.domain.exception.ReferralProblems.InvalidReferralCodeProblem.class);
    }

    private static final Instant REGISTERED_AT_FOR_REFERRAL = Instant.parse("2026-11-13T08:00:00Z");

    private static UserEvent.UserRegisteredEvent registeredAt(Instant at) {
        return new UserEvent.UserRegisteredEvent("user-8", "user@joutio.dev", null, at);
    }
}
