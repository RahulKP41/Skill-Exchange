package com.skillexchange.api.dto.feedback;

import java.time.Instant;

public record FeedbackResponse(
    Long id,
    Long sessionId,
    Long reviewerId,
    String reviewerName,
    Long revieweeId,
    Integer rating,
    String comment,
    Instant createdAt
) {
}

