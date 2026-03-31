package com.skillexchange.api.service;

import com.skillexchange.api.dto.admin.AnalyticsSummaryResponse;
import com.skillexchange.api.entity.Feedback;
import com.skillexchange.api.enums.SessionStatus;
import com.skillexchange.api.repository.ExchangeSessionRepository;
import com.skillexchange.api.repository.FeedbackRepository;
import com.skillexchange.api.repository.PointTransactionRepository;
import com.skillexchange.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final ExchangeSessionRepository exchangeSessionRepository;
    private final FeedbackRepository feedbackRepository;
    private final PointTransactionRepository pointTransactionRepository;

    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse summary() {
        long activeUsers = userRepository.findAll().stream().filter(user -> Boolean.TRUE.equals(user.getActive())).count();
        long completedSessions = exchangeSessionRepository.findAll().stream()
            .filter(session -> session.getStatus() == SessionStatus.COMPLETED)
            .count();
        double averageRating = feedbackRepository.findAll().stream().mapToInt(Feedback::getRating).average().orElse(0);
        return new AnalyticsSummaryResponse(
            activeUsers,
            completedSessions,
            averageRating,
            feedbackRepository.count(),
            pointTransactionRepository.count()
        );
    }
}
