package com.joutio.identity.domain.port.out;

import com.joutio.identity.domain.model.UserReport;
import com.joutio.identity.domain.model.UserReportCount;
import com.joutio.identity.domain.model.UserReportSort;
import com.joutio.identity.domain.model.UserReportStatus;
import com.joutio.identity.domain.model.UserReportSummaryFilter;
import com.joutio.identity.domain.model.UserReportSummaryPage;

import java.util.List;
import java.util.Optional;

/**
 * Port sortant de persistance des signalements de joueurs.
 */
public interface UserReportRepositoryPort {

    void save(UserReport report);

    Optional<UserReport> findById(String reportId);

    List<UserReport> findByTargetUserId(String targetUserId);

    List<UserReport> findOpenByTargetUserId(String targetUserId);

    List<UserReportCount> findCountsByTargetUserIds(List<String> targetUserIds);

    UserReportSummaryPage findSummaryPage(UserReportSummaryFilter filter,
                                          UserReportSort sort,
                                          int page,
                                          int size);

    long countByStatus(UserReportStatus status);

    long countDistinctTargetUserIdByStatus(UserReportStatus status);
}
