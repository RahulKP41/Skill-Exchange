package com.skillexchange.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skillexchange.api.dto.auth.AuthResponse;
import com.skillexchange.api.dto.auth.LoginRequest;
import com.skillexchange.api.dto.auth.RegisterRequest;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.enums.Role;
import com.skillexchange.api.exception.ApiException;
import com.skillexchange.api.repository.RefreshTokenRepository;
import com.skillexchange.api.repository.UserRepository;
import com.skillexchange.api.security.JwtService;
import com.skillexchange.api.service.AuthService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "accessTokenMinutes", 60L);
        ReflectionTestUtils.setField(authService, "refreshTokenDays", 7L);
    }

    @Test
    void registerCreatesUserAndTokens() {
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password123", "Learner");
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(10L);
            user.setRole(Role.USER);
            user.setPointsBalance(1000);
            user.setAverageRating(BigDecimal.ZERO.setScale(2));
            return user;
        });
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token");

        AuthResponse response = authService.register(request);

        assertEquals("access-token", response.accessToken());
        assertEquals("test@example.com", response.user().email());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void loginRejectsBadPassword() {
        User user = User.builder()
            .id(2L)
            .email("user@example.com")
            .passwordHash("hash")
            .role(Role.USER)
            .pointsBalance(1000)
            .averageRating(BigDecimal.ZERO.setScale(2))
            .active(true)
            .build();
        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad-pass", "hash")).thenReturn(false);

        assertThrows(ApiException.class, () -> authService.login(new LoginRequest("user@example.com", "bad-pass")));
    }
}

