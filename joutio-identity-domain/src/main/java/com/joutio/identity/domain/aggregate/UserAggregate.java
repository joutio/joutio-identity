package com.joutio.identity.domain.aggregate;

import com.joutio.identity.domain.command.UserCommand;
import com.joutio.identity.domain.event.UserEvent;
import com.joutio.identity.domain.exception.BirthDateProblems;
import com.joutio.identity.domain.exception.PremiumProblems;
import com.joutio.identity.domain.exception.ReferralProblems;
import com.joutio.identity.domain.exception.UserProblems;
import com.joutio.identity.domain.exception.UserReportProblems;
import com.joutio.identity.domain.model.AccountStatus;
import com.joutio.identity.domain.model.AccountStatusRules;
import com.joutio.identity.domain.model.BirthDateRules;
import com.joutio.identity.domain.model.PremiumRules;
import com.joutio.identity.domain.model.ReferralRules;
import com.joutio.identity.domain.model.SocialProvider;
import com.joutio.identity.domain.model.UserPremium;
import com.joutio.identity.domain.model.UserReportRules;
import com.joutio.identity.domain.model.UserSuspension;
import com.joutio.identity.domain.port.out.EmailClaimPort;
import com.joutio.microservice.core.domain.constant.JoutioConstants;
import com.joutio.identity.domain.event.UserEvent.UserAccountDeletedEvent;
import lombok.Getter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * UserAggregate - Gère le cycle de vie d'un utilisateur.
 *
 * <p>Aucun credential n'est porté par l'agrégat : l'authentification est passwordless
 * (code OTP email) ou sociale (provider OIDC). L'agrégat porte aussi le statut de compte
 * ({@link AccountStatus}), la dernière suspension connue et le droit Premium ({@link UserPremium}).</p>
 */
@Getter
@Aggregate
public class UserAggregate {

    @AggregateIdentifier
    private String userId;

    private String email;

    private Set<SocialProvider> linkedSocialAccounts;

    private Instant lastLoginAt;

    private AccountStatus status = AccountStatus.ACTIVE;

    private UserSuspension suspension;

    private UserPremium premium;

    /** Privée : jamais portée par un événement public autre que {@link UserEvent.UserBirthDateSetEvent}. */
    private LocalDate birthDate;

    private Instant registeredAt;

    /** Parrain, une fois le code appliqué. */
    private String referredBy;

    // Constructeur par défaut requis par Axon
    protected UserAggregate() {
    }

    @CommandHandler
    public UserAggregate(UserCommand.RegisterUserCommand command, EmailClaimPort emailClaimPort) {
        validateRegistration(command.userId(), command.email(), emailClaimPort);

        AggregateLifecycle.apply(
                new UserEvent.UserRegisteredEvent(
                        command.userId(),
                        command.email(),
                        null,
                        Instant.now()
                )
        );
    }

    @CommandHandler
    public UserAggregate(UserCommand.RegisterUserWithSocialCommand command, EmailClaimPort emailClaimPort) {
        validateProvider(command.userId(), command.provider());
        validateRegistration(command.userId(), command.email(), emailClaimPort);

        AggregateLifecycle.apply(
                new UserEvent.UserRegisteredEvent(
                        command.userId(),
                        command.email(),
                        command.provider(),
                        Instant.now()
                )
        );
    }

    @CommandHandler
    public void handle(UserCommand.LinkSocialProviderCommand command) {
        validateProvider(command.userId(), command.provider());

        if (linkedSocialAccounts != null && linkedSocialAccounts.contains(command.provider())) {
            return;
        }

        AggregateLifecycle.apply(
                new UserEvent.SocialProviderLinkedEvent(
                        command.userId(),
                        command.provider(),
                        Instant.now()
                )
        );
    }

    @CommandHandler
    public void handle(UserCommand.RecordUserLoginCommand command) {
        AggregateLifecycle.apply(
                new UserEvent.UserLoggedInEvent(
                        command.userId(),
                        Instant.now()
                )
        );
    }

    @CommandHandler
    public void handle(UserCommand.DeleteAccountCommand command,
                       EmailClaimPort emailClaimPort) {
        if (JoutioConstants.SYSTEM_USER_ID.equals(userId)) {
            throw new UserReportProblems.SystemAccountNotSuspendableProblem(userId);
        }
        if (status == AccountStatus.DELETED) {
            return;
        }
        if (email != null) {
            emailClaimPort.release(email, userId);
        }
        AggregateLifecycle.apply(new UserAccountDeletedEvent(
                userId,
                command.deletionId(),
                command.deletedAt()));
    }

    @CommandHandler
    public void handle(UserCommand.SuspendUserCommand command) {
        if (JoutioConstants.SYSTEM_USER_ID.equals(userId)) {
            throw new UserReportProblems.SystemAccountNotSuspendableProblem(userId);
        }
        if (status == AccountStatus.SUSPENDED
                && AccountStatusRules.isSuspended(status,
                suspension == null ? null : suspension.suspendedUntil(), command.suspendedAt())) {
            throw new UserReportProblems.AlreadySuspendedProblem(userId);
        }
        if (command.reason() == null) {
            throw new UserReportProblems.InvalidSuspensionProblem(userId, "A suspension reason is required");
        }
        try {
            UserReportRules.validateNote(command.note());
            UserReportRules.validateSuspension(command.suspendedAt(), command.suspendedUntil());
        } catch (IllegalArgumentException error) {
            throw new UserReportProblems.InvalidSuspensionProblem(userId, error.getMessage());
        }

        AggregateLifecycle.apply(new UserEvent.UserSuspendedEvent(
                userId,
                email,
                UUID.randomUUID().toString(),
                command.reason(),
                command.note(),
                command.requestedBy(),
                command.suspendedAt(),
                command.suspendedUntil()));
    }

    @CommandHandler
    public void handle(UserCommand.ReinstateUserCommand command) {
        if (status != AccountStatus.SUSPENDED) {
            return;
        }
        try {
            UserReportRules.validateNote(command.note());
        } catch (IllegalArgumentException error) {
            throw new UserReportProblems.InvalidSuspensionProblem(userId, error.getMessage());
        }

        AggregateLifecycle.apply(new UserEvent.UserReinstatedEvent(
                userId,
                email,
                suspension == null ? null : suspension.suspensionId(),
                command.reason(),
                command.note(),
                command.requestedBy(),
                command.reinstatedAt()));
    }

    @CommandHandler
    public void handle(UserCommand.GrantPremiumCommand command) {
        if (status == AccountStatus.DELETED || JoutioConstants.SYSTEM_USER_ID.equals(userId)) {
            throw new PremiumProblems.PremiumNotGrantableProblem(userId);
        }
        if (command.source() == null || command.grantedAt() == null) {
            throw new PremiumProblems.InvalidPremiumGrantProblem(userId, "A source and a grant date are required");
        }
        if (command.premiumUntil() != null && !command.premiumUntil().isAfter(command.grantedAt())) {
            throw new PremiumProblems.InvalidPremiumGrantProblem(userId, "The premium end must be in the future");
        }
        if (PremiumRules.isActive(premium, command.grantedAt())
                && premium.source() == command.source()
                && java.util.Objects.equals(premium.premiumUntil(), command.premiumUntil())) {
            return;
        }
        AggregateLifecycle.apply(new UserEvent.UserPremiumGrantedEvent(
                userId,
                command.source(),
                command.premiumUntil(),
                command.requestedBy(),
                command.grantedAt()));
    }

    @CommandHandler
    public void handle(UserCommand.RevokePremiumCommand command) {
        if (!PremiumRules.isActive(premium, command.revokedAt())) {
            return;
        }
        try {
            UserReportRules.validateNote(command.note());
        } catch (IllegalArgumentException error) {
            throw new PremiumProblems.InvalidPremiumGrantProblem(userId, error.getMessage());
        }
        AggregateLifecycle.apply(new UserEvent.UserPremiumRevokedEvent(
                userId,
                premium.source(),
                command.note(),
                command.requestedBy(),
                command.revokedAt()));
    }

    @CommandHandler
    public void handle(UserCommand.SetBirthDateCommand command) {
        if (status == AccountStatus.DELETED || JoutioConstants.SYSTEM_USER_ID.equals(userId)) {
            throw new BirthDateProblems.BirthDateNotEditableProblem(userId);
        }
        Instant requestedAt = command.requestedAt() == null ? Instant.now() : command.requestedAt();
        LocalDate today = BirthDateRules.today(requestedAt);
        try {
            BirthDateRules.validate(command.birthDate(), today);
        } catch (IllegalArgumentException error) {
            throw new BirthDateProblems.InvalidBirthDateProblem(userId, error.getMessage());
        }
        if (!BirthDateRules.oldEnough(command.birthDate(), today)) {
            throw new BirthDateProblems.UnderageProblem(userId);
        }
        if (command.birthDate().equals(birthDate)) {
            return;
        }
        AggregateLifecycle.apply(new UserEvent.UserBirthDateSetEvent(
                userId,
                command.birthDate(),
                command.requestedBy(),
                requestedAt));
    }

    @CommandHandler
    public void handle(UserCommand.ApplyReferralCommand command) {
        if (status == AccountStatus.DELETED || JoutioConstants.SYSTEM_USER_ID.equals(userId)) {
            throw new ReferralProblems.InvalidReferralCodeProblem(userId);
        }
        if (command.referrerId() == null || command.referrerId().isBlank()
                || JoutioConstants.SYSTEM_USER_ID.equals(command.referrerId())) {
            throw new ReferralProblems.InvalidReferralCodeProblem(userId);
        }
        if (command.referrerId().equals(userId)) {
            throw new ReferralProblems.SelfReferralProblem(userId);
        }
        if (command.referrerId().equals(referredBy)) {
            return;
        }
        if (referredBy != null) {
            throw new ReferralProblems.AlreadyReferredProblem(userId);
        }
        Instant requestedAt = command.requestedAt() == null ? Instant.now() : command.requestedAt();
        if (!ReferralRules.withinWindow(registeredAt, requestedAt)) {
            throw new ReferralProblems.ReferralWindowClosedProblem(userId);
        }
        AggregateLifecycle.apply(new UserEvent.UserReferredEvent(userId, command.referrerId(), requestedAt));
    }

    @EventSourcingHandler
    public void on(UserEvent.UserReferredEvent event) {
        this.referredBy = event.referrerId();
    }

    @EventSourcingHandler
    public void on(UserEvent.UserBirthDateSetEvent event) {
        this.birthDate = event.birthDate();
    }

    @EventSourcingHandler
    public void on(UserEvent.UserRegisteredEvent event) {
        this.linkedSocialAccounts = new HashSet<>();
        this.userId = event.userId();
        this.email = event.email();
        this.status = AccountStatus.ACTIVE;
        this.registeredAt = event.createdAt();

        if (event.provider() != null) {
            this.linkedSocialAccounts.add(event.provider());
        }
    }

    @EventSourcingHandler
    public void on(UserEvent.SocialProviderLinkedEvent event) {
        if (this.linkedSocialAccounts == null) {
            this.linkedSocialAccounts = new HashSet<>();
        }
        this.linkedSocialAccounts.add(event.provider());
    }

    @EventSourcingHandler
    public void on(UserEvent.UserLoggedInEvent event) {
        this.lastLoginAt = event.loggedInAt();
    }

    @EventSourcingHandler
    public void on(UserEvent.UserSuspendedEvent event) {
        this.status = AccountStatus.SUSPENDED;
        this.suspension = new UserSuspension(
                event.suspensionId(),
                event.reason(),
                event.note(),
                event.suspendedBy(),
                event.suspendedAt(),
                event.suspendedUntil(),
                null,
                null,
                null,
                null);
    }

    @EventSourcingHandler
    public void on(UserAccountDeletedEvent event) {
        this.status = AccountStatus.DELETED;
        this.email = "deleted-" + userId + "@invalid.local";
        this.linkedSocialAccounts = new HashSet<>();
        this.suspension = null;
        this.premium = null;
        this.birthDate = null;
        this.lastLoginAt = null;
    }

    @EventSourcingHandler
    public void on(UserEvent.UserPremiumGrantedEvent event) {
        this.premium = new UserPremium(event.source(), event.premiumUntil(), event.grantedBy(), event.grantedAt());
    }

    @EventSourcingHandler
    public void on(UserEvent.UserPremiumRevokedEvent event) {
        this.premium = null;
    }

    @EventSourcingHandler
    public void on(UserEvent.UserReinstatedEvent event) {
        this.status = AccountStatus.ACTIVE;
        if (this.suspension != null) {
            this.suspension = this.suspension.reinstated(
                    event.reinstatedAt(),
                    event.reinstatedBy(),
                    event.reason(),
                    event.note());
        }
    }

    private void validateProvider(String userId, SocialProvider provider) {
        if (provider == null) {
            throw new UserProblems.SocialProviderMissingProblem(userId);
        }
    }

    private void validateRegistration(String userId, String email, EmailClaimPort emailClaimPort) {
        if (email == null || email.trim().isEmpty()) {
            throw new UserProblems.InvalidEmailFormatProblem(userId, email);
        }
        if (!email.contains("@")) {
            throw new UserProblems.InvalidEmailFormatProblem(userId, email);
        }
        if (!emailClaimPort.claim(email, userId)) {
            throw new UserProblems.UserAlreadyExistsProblem(userId, email);
        }
    }
}
