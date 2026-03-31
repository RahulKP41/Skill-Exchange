package com.skillexchange.api.dto.admin;

import java.time.Instant;

public record ModerationReportResponse(
    Long id,
    String reporterName,
    String reportedUserName,
    String reason,
    String details,
    String status,
    Instant createdAt
) {
}

