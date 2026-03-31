package com.skillexchange.api.service;

import com.skillexchange.api.dto.wallet.PointTransactionResponse;
import com.skillexchange.api.dto.wallet.WalletSummaryResponse;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.exception.ApiException;
import com.skillexchange.api.repository.PointTransactionRepository;
import com.skillexchange.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final PointTransactionRepository pointTransactionRepository;

    @Transactional(readOnly = true)
    public WalletSummaryResponse summary(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found."));
        return new WalletSummaryResponse(
            user.getPointsBalance(),
            pointTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(transaction -> new PointTransactionResponse(
                    transaction.getId(),
                    transaction.getTransactionType().name(),
                    transaction.getPointsDelta(),
                    transaction.getBalanceAfter(),
                    transaction.getDescription(),
                    transaction.getCreatedAt()
                ))
                .toList()
        );
    }
}

