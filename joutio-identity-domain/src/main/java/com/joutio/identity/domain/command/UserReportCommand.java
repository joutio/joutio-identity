package com.joutio.identity.domain.command;

import com.joutio.identity.domain.model.UserReportContext;
import com.joutio.identity.domain.model.UserReportReason;
import com.joutio.identity.domain.model.UserReportResolution;
import com.joutio.identity.domain.model.UserReportSnapshot;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.Instant;
import java.util.Set;

public interface UserReportCommand {

    record ReportUserCommand(
            @TargetAggregateIdentifier String threadId,
            String targetUserId,
            String reporterId,
            Set<UserReportReason> reasons,
            String comment,
            UserReportContext context,
            String gameId,
            UserReportSnapshot snapshot,
            Instant reportedAt
    ) implements UserReportCommand {
    }

    record ResolveUserReportCommand(
            @TargetAggregateIdentifier String threadId,
            String reportId,
            UserReportResolution resolution,
            String reviewerId,
            String note,
            Instant reviewedAt
    ) implements UserReportCommand {
    }
}
