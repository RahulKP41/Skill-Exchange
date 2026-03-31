package com.skillexchange.api.dto.auth;

public record AuthUserResponse(
    Long id,
    String fullName,
    String email,
    String role,
    String headline,
    Integer pointsBalance,
    String profilePhotoUrl
) {
}

