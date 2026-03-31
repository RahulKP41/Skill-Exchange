package com.skillexchange.api.repository;

import com.skillexchange.api.entity.ModerationReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationReportRepository extends JpaRepository<ModerationReport, Long> {
    List<ModerationReport> findAllByOrderByCreatedAtDesc();
    long countByStatus(com.skillexchange.api.enums.ReportStatus status);
}

