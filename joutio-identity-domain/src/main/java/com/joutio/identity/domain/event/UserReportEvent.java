package com.joutio.identity.domain.event;

import com.joutio.identity.domain.model.UserReportContext;
import com.joutio.identity.domain.model.UserReportReason;
import com.joutio.identity.domain.model.UserReportSnapshot;
import com.joutio.identity.domain.model.UserReportStatus;

import java.time.Instant;
import java.util.Set;

public interface UserReportEvent {

    String threadId();

    record UserReportedEvent(
            String threadId,
            String reportId,
            String targetUserId,
            String reporterId,
            Set<UserReportReason> reasons,
            String comment,
            UserReportContext context,
            String gameId,
            UserReportSnapshot snapshot,
            Instant reportedAt
    ) implements UserReportEvent {
    }

    record UserReportReviewedEvent(
            String threadId,
            String reportId,
            UserReportStatus status,
            String reviewerId,
            String note,
            Instant reviewedAt
    ) implements UserReportEvent {
    }
}
