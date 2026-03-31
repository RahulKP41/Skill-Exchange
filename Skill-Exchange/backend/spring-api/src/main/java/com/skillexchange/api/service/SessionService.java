package com.skillexchange.api.service;

import com.skillexchange.api.dto.session.CompleteSessionRequest;
import com.skillexchange.api.dto.session.CreateSessionRequest;
import com.skillexchange.api.dto.session.SessionResponse;
import com.skillexchange.api.entity.ExchangeRequest;
import com.skillexchange.api.entity.ExchangeSession;
import com.skillexchange.api.entity.PointTransaction;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.enums.NotificationType;
import com.skillexchange.api.enums.RequestStatus;
import com.skillexchange.api.enums.SessionStatus;
import com.skillexchange.api.enums.TransactionType;
import com.skillexchange.api.exception.ApiException;
import com.skillexchange.api.repository.ExchangeRequestRepository;
import com.skillexchange.api.repository.ExchangeSessionRepository;
import com.skillexchange.api.repository.PointTransactionRepository;
import com.skillexchange.api.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final ExchangeSessionRepository exchangeSessionRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Value("${app.jitsi-base-url}")
    private String jitsiBaseUrl;

    @Transactional(readOnly = true)
    public List<SessionResponse> listForUser(Long userId) {
        return exchangeSessionRepository.findByRequestSenderIdOrRequestReceiverIdOrderByScheduledAtDesc(userId, userId).stream()
            .map(session -> mapSession(session, userId))
            .toList();
    }

    @Transactional
    public SessionResponse create(Long userId, CreateSessionRequest request) {
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(request.requestId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Exchange request not found."));
        if (!Objects.equals(exchangeRequest.getSender().getId(), userId) && !Objects.equals(exchangeRequest.getReceiver().getId(), userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You are not part of this exchange request.");
        }
        if (exchangeRequest.getStatus() != RequestStatus.ACCEPTED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only accepted requests can be scheduled.");
        }
        if (exchangeSessionRepository.findByRequestId(exchangeRequest.getId()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "A session already exists for this request.");
        }

        User sender = exchangeRequest.getSender();
        if (sender.getPointsBalance() < exchangeRequest.getPointsCost()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "The sender does not have enough points to schedule this session.");
        }

        sender.setPointsBalance(sender.getPointsBalance() - exchangeRequest.getPointsCost());
        userRepository.save(sender);

        ExchangeSession session = exchangeSessionRepository.save(ExchangeSession.builder()
            .request(exchangeRequest)
            .scheduledAt(request.scheduledAt())
            .durationMinutes(request.durationMinutes() == null ? 60 : request.durationMinutes())
            .meetingLink(buildMeetingLink(exchangeRequest.getId()))
            .agenda(request.agenda())
            .status(SessionStatus.SCHEDULED)
            .build());

        pointTransactionRepository.save(PointTransaction.builder()
            .user(sender)
            .session(session)
            .transactionType(TransactionType.DEBIT)
            .pointsDelta(-exchangeRequest.getPointsCost())
            .balanceAfter(sender.getPointsBalance())
            .description("Reserved points for session #" + session.getId())
            .build());

        notificationService.createNotification(
            sender,
            "Session scheduled",
            "Your session with " + exchangeRequest.getReceiver().getFullName() + " is confirmed.",
            NotificationType.SESSION
        );
        notificationService.createNotification(
            exchangeRequest.getReceiver(),
            "New session booked",
            "You have a new session scheduled with " + sender.getFullName() + ".",
            NotificationType.SESSION
        );

        return mapSession(session, userId);
    }

    @Transactional
    public SessionResponse complete(Long userId, Long sessionId, CompleteSessionRequest request) {
        ExchangeSession session = exchangeSessionRepository.findById(sessionId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Session not found."));
        ExchangeRequest exchangeRequest = session.getRequest();
        if (!Objects.equals(exchangeRequest.getSender().getId(), userId) && !Objects.equals(exchangeRequest.getReceiver().getId(), userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You are not part of this session.");
        }
        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new ApiException(HttpStatus.CONFLICT, "This session is already completed.");
        }

        User receiver = exchangeRequest.getReceiver();
        receiver.setPointsBalance(receiver.getPointsBalance() + exchangeRequest.getPointsCost());
        userRepository.save(receiver);

        session.setStatus(SessionStatus.COMPLETED);
        session.setCompletedAt(Instant.now());
        session.setCompletionNotes(request.completionNotes());
        exchangeSessionRepository.save(session);

        exchangeRequest.setStatus(RequestStatus.COMPLETED);
        exchangeRequestRepository.save(exchangeRequest);

        pointTransactionRepository.save(PointTransaction.builder()
            .user(receiver)
            .session(session)
            .transactionType(TransactionType.CREDIT)
            .pointsDelta(exchangeRequest.getPointsCost())
            .balanceAfter(receiver.getPointsBalance())
            .description("Teaching reward for session #" + session.getId())
            .build());

        notificationService.createNotification(
            receiver,
            "Session completed",
            "Your wallet was credited for completing the session.",
            NotificationType.SESSION
        );
        notificationService.createNotification(
            exchangeRequest.getSender(),
            "Feedback unlocked",
            "Your session is complete. You can now leave feedback.",
            NotificationType.FEEDBACK
        );

        return mapSession(session, userId);
    }

    private String buildMeetingLink(Long requestId) {
        return jitsiBaseUrl + "/skill-exchange-" + requestId + "-" + UUID.randomUUID().toString().substring(0, 8).toLowerCase(Locale.ROOT);
    }

    private SessionResponse mapSession(ExchangeSession session, Long currentUserId) {
        ExchangeRequest request = session.getRequest();
        boolean currentUserIsSender = Objects.equals(request.getSender().getId(), currentUserId);
        String partnerName = currentUserIsSender ? request.getReceiver().getFullName() : request.getSender().getFullName();
        String skillFocus = request.getRequestedUserSkill().getSkill().getName();
        return new SessionResponse(
            session.getId(),
            request.getId(),
            partnerName,
            skillFocus,
            session.getScheduledAt(),
            session.getDurationMinutes(),
            session.getMeetingLink(),
            session.getAgenda(),
            session.getStatus().name()
        );
    }
}

