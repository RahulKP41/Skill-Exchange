package com.skillexchange.api.dto.wallet;

import java.util.List;

public record WalletSummaryResponse(
    Integer currentBalance,
    List<PointTransactionResponse> transactions
) {
}

