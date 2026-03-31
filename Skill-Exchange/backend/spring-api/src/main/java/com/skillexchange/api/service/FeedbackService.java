package com.skillexchange.api.service;

import com.skillexchange.api.dto.feedback.CreateFeedbackRequest;
import com.skillexchange.api.dto.feedback.FeedbackResponse;
import com.skillexchange.api.entity.ExchangeRequest;
import com.skillexchange.api.entity.ExchangeSession;
import com.skillexchange.api.entity.Feedback;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.enums.NotificationType;
import com.skillexchange.api.enums.SessionStatus;
import com.skillexchange.api.exception.ApiException;
import com.skillexchange.api.repository.ExchangeSessionRepository;
import com.skillexchange.api.repository.FeedbackRepository;
import com.skillexchange.api.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ExchangeSessionRepository exchangeSessionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public FeedbackResponse create(Long userId, CreateFeedbackRequest request) {
        ExchangeSession session = exchangeSessionRepository.findById(request.sessionId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Session not found."));
        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Feedback is only available after a completed session.");
        }
        ExchangeRequest exchangeRequest = session.getRequest();
        if (!Objects.equals(exchangeRequest.getSender().getId(), userId) && !Objects.equals(exchangeRequest.getReceiver().getId(), userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You are not part of this session.");
        }
        Long expectedRevieweeId = Objects.equals(exchangeRequest.getSender().getId(), userId)
            ? exchangeRequest.getReceiver().getId()
            : exchangeRequest.getSender().getId();
        if (!Objects.equals(expectedRevieweeId, request.revieweeId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "You can only review the other participant.");
        }
        feedbackRepository.findBySessionIdAndReviewerId(session.getId(), userId).ifPresent(existing -> {
            throw new ApiException(HttpStatus.CONFLICT, "You have already submitted feedback for this session.");
        });

        User reviewer = userRepository.findById(userId).orElseThrow();
        User reviewee = userRepository.findById(request.revieweeId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Reviewee not found."));
        Feedback saved = feedbackRepository.save(Feedback.builder()
            .session(session)
            .reviewer(reviewer)
            .reviewee(reviewee)
            .rating(request.rating())
            .comment(request.comment())
            .build());

        recalculateRating(reviewee);
        notificationService.createNotification(
            reviewee,
            "New feedback received",
            reviewer.getFullName() + " left feedback for your recent session.",
            NotificationType.FEEDBACK
        );

        return map(saved);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> listForUser(Long userId) {
        return feedbackRepository.findByRevieweeIdOrderByCreatedAtDesc(userId).stream().map(this::map).toList();
    }

    private void recalculateRating(User reviewee) {
        List<Feedback> entries = feedbackRepository.findByRevieweeIdOrderByCreatedAtDesc(reviewee.getId());
        double average = entries.stream().mapToInt(Feedback::getRating).average().orElse(0);
        reviewee.setAverageRating(BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
        reviewee.setTotalReviews(entries.size());
        userRepository.save(reviewee);
    }

    private FeedbackResponse map(Feedback feedback) {
        return new FeedbackResponse(
            feedback.getId(),
            feedback.getSession().getId(),
            feedback.getReviewer().getId(),
            feedback.getReviewer().getFullName(),
            feedback.getReviewee().getId(),
            feedback.getRating(),
            feedback.getComment(),
            feedback.getCreatedAt()
        );
    }
}

