package com.skillexchange.api.repository;

import com.skillexchange.api.entity.ExchangeSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeSessionRepository extends JpaRepository<ExchangeSession, Long> {
    List<ExchangeSession> findByRequestSenderIdOrRequestReceiverIdOrderByScheduledAtDesc(Long senderId, Long receiverId);
    Optional<ExchangeSession> findByRequestId(Long requestId);
}
