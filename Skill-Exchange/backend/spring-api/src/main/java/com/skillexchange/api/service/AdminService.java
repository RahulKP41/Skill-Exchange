package com.skillexchange.api.service;

import com.skillexchange.api.dto.admin.AdminOverviewResponse;
import com.skillexchange.api.dto.admin.ModerationReportResponse;
import com.skillexchange.api.entity.AdminAuditLog;
import com.skillexchange.api.entity.ModerationReport;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.enums.ReportStatus;
import com.skillexchange.api.exception.ApiException;
import com.skillexchange.api.repository.AdminAuditLogRepository;
import com.skillexchange.api.repository.ExchangeRequestRepository;
import com.skillexchange.api.repository.ExchangeSessionRepository;
import com.skillexchange.api.repository.ModerationReportRepository;
import com.skillexchange.api.repository.NotificationRepository;
import com.skillexchange.api.repository.SkillRepository;
import com.skillexchange.api.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final ExchangeSessionRepository exchangeSessionRepository;
    private final ModerationReportRepository moderationReportRepository;
    private final NotificationRepository notificationRepository;
    private final AdminAuditLogRepository adminAuditLogRepository;

    @Transactional(readOnly = true)
    public AdminOverviewResponse overview(Long adminId) {
        return new AdminOverviewResponse(
            userRepository.count(),
            skillRepository.count(),
            exchangeRequestRepository.count(),
            exchangeSessionRepository.count(),
            moderationReportRepository.countByStatus(ReportStatus.OPEN),
            notificationRepository.countByUserIdAndReadFalse(adminId)
        );
    }

    @Transactional(readOnly = true)
    public List<ModerationReportResponse> reports() {
        return moderationReportRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(report -> new ModerationReportResponse(
                report.getId(),
                report.getReporter().getFullName(),
                report.getReportedUser().getFullName(),
                report.getReason(),
                report.getDetails(),
                report.getStatus().name(),
                report.getCreatedAt()
            ))
            .toList();
    }

    @Transactional
    public ModerationReportResponse updateReport(Long adminId, Long reportId, String statusValue) {
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Admin user not found."));
        ModerationReport report = moderationReportRepository.findById(reportId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found."));
        ReportStatus status = ReportStatus.valueOf(statusValue.toUpperCase());
        report.setStatus(status);
        moderationReportRepository.save(report);
        adminAuditLogRepository.save(AdminAuditLog.builder()
            .admin(admin)
            .action("REPORT_STATUS_UPDATED")
            .entityType("MODERATION_REPORT")
            .entityId(report.getId())
            .details("Set status to " + status.name())
            .build());
        return new ModerationReportResponse(
            report.getId(),
            report.getReporter().getFullName(),
            report.getReportedUser().getFullName(),
            report.getReason(),
            report.getDetails(),
            report.getStatus().name(),
            report.getCreatedAt()
        );
    }
}

