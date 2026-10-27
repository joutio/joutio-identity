package com.joutio.identity.domain.query;

import com.joutio.identity.domain.model.UserReportSort;
import com.joutio.identity.domain.model.UserReportSummaryFilter;

public interface UserReportQuery {

    record GetUserReportByIdQuery(String reportId) {
    }

    record GetUserReportsByTargetQuery(String targetUserId) {
    }

    record GetUserReportCountsQuery(java.util.List<String> targetUserIds) {
    }

    record GetReportedUserPageQuery(
            UserReportSummaryFilter filter,
            UserReportSort sort,
            int page,
            int size
    ) {
    }

    record GetUserReportStatsQuery() {
    }
}
