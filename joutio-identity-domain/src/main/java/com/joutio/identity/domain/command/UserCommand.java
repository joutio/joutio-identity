package com.joutio.identity.domain.command;

import com.joutio.identity.domain.model.PremiumSource;
import com.joutio.identity.domain.model.SocialProvider;
import com.joutio.identity.domain.model.SuspensionReinstatementReason;
import com.joutio.identity.domain.model.UserSuspensionReason;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.Instant;

public interface UserCommand {

    String userId();

    /**
     * Enregistre un utilisateur sans credential (passwordless / compte système).
     * L'authentification se fait ensuite par code à usage unique (OTP) ou provider social.
     */
    record RegisterUserCommand(
            @TargetAggregateIdentifier String userId,
            String email
    ) implements UserCommand {
    }

    record RegisterUserWithSocialCommand(
            @TargetAggregateIdentifier String userId,
            String email,
            SocialProvider provider
    ) implements UserCommand {
    }

    record LinkSocialProviderCommand(
            @TargetAggregateIdentifier String userId,
            SocialProvider provider
    ) implements UserCommand {
    }

    /**
     * Enregistre une connexion réussie d'un utilisateur existant.
     */
    record RecordUserLoginCommand(
            @TargetAggregateIdentifier String userId
    ) implements UserCommand {
    }

    /**
     * Supprime définitivement le compte : l'identité est purgée (email libéré, providers
     * détachés), le statut passe à {@code DELETED} et un événement transverse est publié pour
     * que chaque service supprime ou anonymise ses projections.
     */
    record DeleteAccountCommand(
            @TargetAggregateIdentifier String userId,
            String deletionId,
            Instant deletedAt
    ) implements UserCommand {
    }

    /**
     * Suspend un compte. {@code suspendedUntil} nul = suspension définitive.
     *
     * <p>La décision (motif, durée, acteur) est préparée par l'appelant : l'agrégat ne fait
     * aucun I/O et se contente de valider les invariants.</p>
     */
    record SuspendUserCommand(
            @TargetAggregateIdentifier String userId,
            UserSuspensionReason reason,
            String note,
            String requestedBy,
            Instant suspendedUntil,
            Instant suspendedAt
    ) implements UserCommand {
    }

    /**
     * Réintègre un compte suspendu. Idempotent : sans effet si le compte est déjà actif.
     */
    record ReinstateUserCommand(
            @TargetAggregateIdentifier String userId,
            SuspensionReinstatementReason reason,
            String note,
            String requestedBy,
            Instant reinstatedAt
    ) implements UserCommand {
    }

    /**
     * Accorde (ou remplace) le droit Premium. {@code premiumUntil} nul = sans échéance.
     *
     * <p>Émise par un administrateur (source {@code MANUAL}) puis, à l'ouverture des paiements,
     * par la traduction des événements d'abonnement de {@code joutio-billing}. Idempotente :
     * un droit identique déjà actif n'émet rien.</p>
     */
    record GrantPremiumCommand(
            @TargetAggregateIdentifier String userId,
            PremiumSource source,
            Instant premiumUntil,
            String requestedBy,
            Instant grantedAt
    ) implements UserCommand {
    }

    /**
     * Retire le droit Premium. Idempotente : sans effet si aucun droit n'est actif.
     */
    record RevokePremiumCommand(
            @TargetAggregateIdentifier String userId,
            String note,
            String requestedBy,
            Instant revokedAt
    ) implements UserCommand {
    }
}
