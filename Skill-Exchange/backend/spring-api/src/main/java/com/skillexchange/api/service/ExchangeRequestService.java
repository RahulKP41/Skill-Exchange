package com.skillexchange.api.service;

import com.skillexchange.api.dto.request.CreateExchangeRequestRequest;
import com.skillexchange.api.dto.request.ExchangeRequestResponse;
import com.skillexchange.api.entity.ExchangeRequest;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.entity.UserSkill;
import com.skillexchange.api.enums.NotificationType;
import com.skillexchange.api.enums.RequestStatus;
import com.skillexchange.api.enums.SkillType;
import com.skillexchange.api.exception.ApiException;
import com.skillexchange.api.repository.ExchangeRequestRepository;
import com.skillexchange.api.repository.UserRepository;
import com.skillexchange.api.repository.UserSkillRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExchangeRequestService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<ExchangeRequestResponse> listForUser(Long userId) {
        return exchangeRequestRepository.findBySenderIdOrReceiverIdOrderByCreatedAtDesc(userId, userId).stream()
            .map(this::mapRequest)
            .toList();
    }

    @Transactional
    public ExchangeRequestResponse create(Long userId, CreateExchangeRequestRequest request) {
        User sender = requiredUser(userId);
        User receiver = requiredUser(request.receiverId());
        if (Objects.equals(sender.getId(), receiver.getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "You cannot create an exchange with yourself.");
        }

        UserSkill offered = userSkillRepository.findByIdAndUserId(request.offeredUserSkillId(), userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Offered skill was not found."));
        UserSkill requested = userSkillRepository.findById(request.requestedUserSkillId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Requested skill was not found."));

        if (!Objects.equals(requested.getUser().getId(), receiver.getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Requested skill must belong to the selected receiver.");
        }
        if (offered.getSkillType() != SkillType.TEACH || requested.getSkillType() != SkillType.TEACH) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Exchange requests must use teachable skills.");
        }

        boolean senderWantsRequestedSkill = userSkillRepository.findByUserIdAndSkillType(userId, SkillType.LEARN).stream()
            .anyMatch(skill -> Objects.equals(skill.getSkill().getId(), requested.getSkill().getId()));
        boolean receiverWantsOfferedSkill = userSkillRepository.findByUserIdAndSkillType(receiver.getId(), SkillType.LEARN).stream()
            .anyMatch(skill -> Objects.equals(skill.getSkill().getId(), offered.getSkill().getId()));
        if (!senderWantsRequestedSkill || !receiverWantsOfferedSkill) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Both sides need a compatible teach/learn pairing for this exchange.");
        }

        ExchangeRequest saved = exchangeRequestRepository.save(ExchangeRequest.builder()
            .sender(sender)
            .receiver(receiver)
            .offeredUserSkill(offered)
            .requestedUserSkill(requested)
            .status(RequestStatus.PENDING)
            .message(request.message())
            .preferredDateTime(request.preferredDateTime())
            .pointsCost(350)
            .build());

        notificationService.createNotification(
            receiver,
            "New exchange request",
            sender.getFullName() + " wants to exchange " + offered.getSkill().getName() + " for " + requested.getSkill().getName() + ".",
            NotificationType.REQUEST
        );
        return mapRequest(saved);
    }

    @Transactional
    public ExchangeRequestResponse updateStatus(Long userId, Long requestId, String nextStatus) {
        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Exchange request not found."));
        RequestStatus status = RequestStatus.valueOf(nextStatus.toUpperCase());

        switch (status) {
            case ACCEPTED, REJECTED -> {
                if (!Objects.equals(request.getReceiver().getId(), userId)) {
                    throw new ApiException(HttpStatus.FORBIDDEN, "Only the receiver can accept or reject this request.");
                }
            }
            case CANCELLED -> {
                if (!Objects.equals(request.getSender().getId(), userId)) {
                    throw new ApiException(HttpStatus.FORBIDDEN, "Only the sender can cancel this request.");
                }
            }
            default -> throw new ApiException(HttpStatus.BAD_REQUEST, "This status transition is not supported here.");
        }

        request.setStatus(status);
        exchangeRequestRepository.save(request);
        notificationService.createNotification(
            request.getSender(),
            "Request updated",
            "Your exchange request is now " + status.name().toLowerCase() + ".",
            NotificationType.REQUEST
        );
        return mapRequest(request);
    }

    private User requiredUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found."));
    }

    private ExchangeRequestResponse mapRequest(ExchangeRequest request) {
        return new ExchangeRequestResponse(
            request.getId(),
            request.getSender().getId(),
            request.getSender().getFullName(),
            request.getReceiver().getId(),
            request.getReceiver().getFullName(),
            request.getOfferedUserSkill().getId(),
            request.getOfferedUserSkill().getSkill().getName(),
            request.getRequestedUserSkill().getId(),
            request.getRequestedUserSkill().getSkill().getName(),
            request.getStatus().name(),
            request.getMessage(),
            request.getPreferredDateTime(),
            request.getPointsCost(),
            request.getCreatedAt()
        );
    }
}

