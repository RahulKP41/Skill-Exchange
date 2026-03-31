package com.skillexchange.api.dto.profile;

public record PublicUserResponse(
    Long id,
    String fullName,
    String headline,
    String location,
    String bio,
    String profilePhotoUrl,
    Integer pointsBalance,
    String averageRating
) {
}

