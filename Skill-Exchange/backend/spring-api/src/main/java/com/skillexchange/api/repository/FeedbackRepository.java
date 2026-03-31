package com.skillexchange.api.repository;

import com.skillexchange.api.entity.Feedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByRevieweeIdOrderByCreatedAtDesc(Long revieweeId);
    Optional<Feedback> findBySessionIdAndReviewerId(Long sessionId, Long reviewerId);
    long countByRevieweeId(Long revieweeId);
}

