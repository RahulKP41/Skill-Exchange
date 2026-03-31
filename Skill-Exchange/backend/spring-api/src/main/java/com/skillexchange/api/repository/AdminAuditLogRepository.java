package com.skillexchange.api.repository;

import com.skillexchange.api.entity.AdminAuditLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
    List<AdminAuditLog> findTop20ByOrderByCreatedAtDesc();
}
