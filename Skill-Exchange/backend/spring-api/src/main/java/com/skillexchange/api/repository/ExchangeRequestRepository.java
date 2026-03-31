package com.skillexchange.api.repository;

import com.skillexchange.api.entity.ExchangeRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
    List<ExchangeRequest> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId);
    Optional<ExchangeRequest> findByIdAndReceiverId(Long id, Long receiverId);
    Optional<ExchangeRequest> findByIdAndSenderId(Long id, Long senderId);
}

