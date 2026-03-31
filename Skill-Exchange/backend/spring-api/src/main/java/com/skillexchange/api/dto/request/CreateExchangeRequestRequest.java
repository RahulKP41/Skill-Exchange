package com.skillexchange.api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateExchangeRequestRequest(
    @NotNull(message = "Receiver id is required")
    Long receiverId,
    @NotNull(message = "Offered user skill id is required")
    Long offeredUserSkillId,
    @NotNull(message = "Requested user skill id is required")
    Long requestedUserSkillId,
    String message,
    Instant preferredDateTime
) {
}

