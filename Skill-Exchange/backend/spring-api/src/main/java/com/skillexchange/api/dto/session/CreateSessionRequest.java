package com.skillexchange.api.dto.session;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateSessionRequest(
    @NotNull(message = "Request id is required")
    Long requestId,
    @NotNull(message = "Scheduled time is required")
    Instant scheduledAt,
    @Min(value = 30, message = "Duration must be at least 30 minutes")
    Integer durationMinutes,
    String agenda
) {
}

