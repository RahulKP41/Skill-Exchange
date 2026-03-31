package com.skillexchange.api.dto.auth;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    long expiresInSeconds,
    AuthUserResponse user
) {
}

