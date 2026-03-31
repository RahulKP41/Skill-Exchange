package com.skillexchange.api.dto.session;

import java.time.Instant;

public record SessionResponse(
    Long id,
    Long requestId,
    String partnerName,
    String skillFocus,
    Instant scheduledAt,
    Integer durationMinutes,
    String meetingLink,
    String agenda,
    String status
) {
}

