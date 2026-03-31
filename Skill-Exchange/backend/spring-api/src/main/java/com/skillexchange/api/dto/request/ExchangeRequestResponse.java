package com.skillexchange.api.dto.request;

import java.time.Instant;

public record ExchangeRequestResponse(
    Long id,
    Long senderId,
    String senderName,
    Long receiverId,
    String receiverName,
    Long offeredUserSkillId,
    String offeredSkillName,
    Long requestedUserSkillId,
    String requestedSkillName,
    String status,
    String message,
    Instant preferredDateTime,
    Integer pointsCost,
    Instant createdAt
) {
}

