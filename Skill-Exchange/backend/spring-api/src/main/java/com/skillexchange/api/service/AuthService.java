package com.skillexchange.api.service;

import com.skillexchange.api.dto.auth.AuthResponse;
import com.skillexchange.api.dto.auth.AuthUserResponse;
import com.skillexchange.api.dto.auth.LoginRequest;
import com.skillexchange.api.dto.auth.RefreshRequest;
import com.skillexchange.api.dto.auth.RegisterRequest;
import com.skillexchange.api.entity.RefreshToken;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.enums.Role;
import com.skillexchange.api.exception.ApiException;
import com.skillexchange.api.repository.RefreshTokenRepository;
import com.skillexchange.api.repository.UserRepository;
import com.skillexchange.api.security.JwtService;
import com.skillexchange.api.util.HashingUtil;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.access-token-minutes}")
    private long accessTokenMinutes;

    @Value("${app.refresh-token-days}")
    private long refreshTokenDays;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        userRepository.findByEmailIgnoreCase(request.email()).ifPresent(existing -> {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists.");
        });

        User user = userRepository.save(User.builder()
            .fullName(request.fullName())
            .email(request.email().trim().toLowerCase())
            .passwordHash(passwordEncoder.encode(request.password()))
            .headline(request.headline())
            .role(Role.USER)
            .pointsBalance(1000)
            .averageRating(BigDecimal.ZERO.setScale(2))
            .totalReviews(0)
            .active(true)
            .build());

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim().toLowerCase())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        String tokenHash = HashingUtil.sha256(request.refreshToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token is invalid."));
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token has expired.");
        }
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return issueTokens(stored.getUser());
    }

    @Transactional(readOnly = true)
    public AuthUserResponse me(Long userId) {
        return mapUser(userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found.")));
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = UUID.randomUUID() + "." + UUID.randomUUID();
        refreshTokenRepository.save(RefreshToken.builder()
            .user(user)
            .tokenHash(HashingUtil.sha256(refreshToken))
            .expiresAt(Instant.now().plusSeconds(refreshTokenDays * 24 * 3600))
            .revoked(false)
            .build());
        return new AuthResponse(accessToken, refreshToken, accessTokenMinutes * 60, mapUser(user));
    }

    private AuthUserResponse mapUser(User user) {
        return new AuthUserResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole().name(),
            user.getHeadline(),
            user.getPointsBalance(),
            user.getProfilePhotoUrl()
        );
    }
}
