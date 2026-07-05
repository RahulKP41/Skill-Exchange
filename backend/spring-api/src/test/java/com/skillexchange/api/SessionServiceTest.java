package com.skillexchange.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skillexchange.api.dto.session.CompleteSessionRequest;
import com.skillexchange.api.entity.ExchangeRequest;
import com.skillexchange.api.entity.ExchangeSession;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.enums.RequestStatus;
import com.skillexchange.api.enums.Role;
import com.skillexchange.api.enums.SessionStatus;
import com.skillexchange.api.repository.ExchangeRequestRepository;
import com.skillexchange.api.repository.ExchangeSessionRepository;
import com.skillexchange.api.repository.PointTransactionRepository;
import com.skillexchange.api.repository.UserRepository;
import com.skillexchange.api.service.NotificationService;
import com.skillexchange.api.service.SessionService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private ExchangeSessionRepository exchangeSessionRepository;

    @Mock
    private ExchangeRequestRepository exchangeRequestRepository;

    @Mock
    private PointTransactionRepository pointTransactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void completingSessionCreditsReceiverAndMarksRequestComplete() {
        User sender = User.builder().id(1L).fullName("Aarav").pointsBalance(650).role(Role.USER).averageRating(BigDecimal.ZERO.setScale(2)).active(true).build();
        User receiver = User.builder().id(2L).fullName("Maya").pointsBalance(900).role(Role.USER).averageRating(BigDecimal.ZERO.setScale(2)).active(true).build();
        ExchangeRequest request = ExchangeRequest.builder()
            .id(10L)
            .sender(sender)
            .receiver(receiver)
            .status(RequestStatus.ACCEPTED)
            .pointsCost(350)
            .build();
        ExchangeSession session = ExchangeSession.builder()
            .id(22L)
            .request(request)
            .status(SessionStatus.SCHEDULED)
            .build();

        when(exchangeSessionRepository.findById(22L)).thenReturn(Optional.of(session));

        sessionService.complete(1L, 22L, new CompleteSessionRequest("Great session"));

        assertEquals(1250, receiver.getPointsBalance());
        assertEquals(SessionStatus.COMPLETED, session.getStatus());
        assertEquals(RequestStatus.COMPLETED, request.getStatus());
        verify(pointTransactionRepository).save(any());
    }
}
