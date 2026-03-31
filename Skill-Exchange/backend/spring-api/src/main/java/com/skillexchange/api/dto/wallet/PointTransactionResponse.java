package com.skillexchange.api.dto.wallet;

import java.time.Instant;

public record PointTransactionResponse(
    Long id,
    String transactionType,
    Integer pointsDelta,
    Integer balanceAfter,
    String description,
    Instant createdAt
) {
}

