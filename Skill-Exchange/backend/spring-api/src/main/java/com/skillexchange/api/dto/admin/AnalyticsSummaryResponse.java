package com.skillexchange.api.dto.admin;

public record AnalyticsSummaryResponse(
    long activeUsers,
    long completedSessions,
    double averageRating,
    long totalFeedbackEntries,
    long totalPointTransactions
) {
}
