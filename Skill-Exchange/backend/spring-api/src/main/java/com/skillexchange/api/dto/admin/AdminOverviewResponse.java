package com.skillexchange.api.dto.admin;

public record AdminOverviewResponse(
    long totalUsers,
    long totalSkills,
    long totalRequests,
    long totalSessions,
    long openReports,
    long unreadNotifications
) {
}

