package com.joutio.identity.application.projection;

import com.joutio.identity.domain.event.UserEvent;
import com.joutio.identity.domain.model.AccountStatus;
import com.joutio.identity.domain.model.SuspensionReinstatementReason;
import com.joutio.identity.domain.model.User;
import com.joutio.identity.domain.model.UserSuspension;
import com.joutio.identity.domain.model.UserSuspensionReason;
import com.joutio.axon.autoconfigure.erasure.AxonEventStoreErasureService;
import com.joutio.identity.application.service.AccountDeletionCleanupService;
import com.joutio.identity.domain.port.out.UserRepositoryPort;
import com.joutio.identity.domain.event.UserEvent.UserAccountDeletedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserProjectionTest {

    private static final Instant AT = Instant.parse("2026-10-03T18:02:00Z");

    private final UserRepositoryPort repository = mock(UserRepositoryPort.class);
    private final AccountDeletionCleanupService deletionCleanupService = mock(AccountDeletionCleanupService.class);
    private final AxonEventStoreErasureService eventStoreErasureService = mock(AxonEventStoreErasureService.class);
    private final UserProjection projection = new UserProjection(
            repository, deletionCleanupService, eventStoreErasureService);

    @Test
    void suspension_updatesStatusAndMetadata() {
        when(repository.findById("user-1")).thenReturn(Optional.of(activeUser()));

        projection.on(new UserEvent.UserSuspendedEvent(
                "user-1", "user@joutio.dev", "s1",
                UserSuspensionReason.HARASSMENT, "internal", "admin-1", AT, null));

        User saved = captureSaved();
        assertThat(saved.status()).isEqualTo(AccountStatus.SUSPENDED);
        assertThat(saved.suspension().suspensionId()).isEqualTo("s1");
        assertThat(saved.suspension().reason()).isEqualTo(UserSuspensionReason.HARASSMENT);
        assertThat(saved.suspension().permanent()).isTrue();
    }

    @Test
    void accountDeletion_anonymizesAccountAndPurgesEventStore() {
        when(repository.findById("user-1")).thenReturn(Optional.of(activeUser()));

        projection.on(new UserAccountDeletedEvent(
                "user-1", "deletion-1", AT.plusSeconds(120)));

        User saved = captureSaved();
        assertThat(saved.status()).isEqualTo(AccountStatus.DELETED);
        assertThat(saved.email()).isEqualTo("deleted-user-1@invalid.local");
        assertThat(saved.linkedSocialAccounts()).isEmpty();
        verify(deletionCleanupService).cleanup(
                "user-1", "user@joutio.dev", "deletion-1", AT.plusSeconds(120));
        verify(eventStoreErasureService).eraseAggregates(java.util.List.of("user-1"));
    }

    @Test
    void reinstatement_returnsToActiveAndKeepsHistory() {
        User suspended = new User(
                "user-1", "user@joutio.dev", Set.of(), AT, AccountStatus.SUSPENDED,
                new UserSuspension("s1", UserSuspensionReason.SPAM, "note", "admin-1",
                        AT, null, null, null, null, null));
        when(repository.findById("user-1")).thenReturn(Optional.of(suspended));

        projection.on(new UserEvent.UserReinstatedEvent(
                "user-1", "user@joutio.dev", "s1",
                SuspensionReinstatementReason.MANUAL, "appeal", "admin-1", AT.plusSeconds(60)));

        User saved = captureSaved();
        assertThat(saved.status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(saved.suspension().reinstatedAt()).isEqualTo(AT.plusSeconds(60));
        assertThat(saved.suspension().reinstatedBy()).isEqualTo("admin-1");
        assertThat(saved.suspension().reinstatementNote()).isEqualTo("appeal");
    }

    @Test
    void socialProviderLink_preservesSuspensionStatus() {
        User suspended = new User(
                "user-1", "user@joutio.dev", Set.of(), AT, AccountStatus.SUSPENDED,
                new UserSuspension("s1", UserSuspensionReason.SPAM, null, "admin-1",
                        AT, null, null, null, null, null));
        when(repository.findById("user-1")).thenReturn(Optional.of(suspended));

        projection.on(new UserEvent.SocialProviderLinkedEvent(
                "user-1", com.joutio.identity.domain.model.SocialProvider.GOOGLE, AT));

        User saved = captureSaved();
        assertThat(saved.status()).isEqualTo(AccountStatus.SUSPENDED);
        assertThat(saved.suspension().suspensionId()).isEqualTo("s1");
    }

    private User captureSaved() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }

    private static User activeUser() {
        return new User("user-1", "user@joutio.dev", Set.of(), AT);
    }
}
