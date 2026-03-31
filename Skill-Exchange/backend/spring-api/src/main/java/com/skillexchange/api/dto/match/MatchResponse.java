package com.skillexchange.api.dto.match;

public record MatchResponse(
    Long userId,
    String fullName,
    String headline,
    String location,
    String profilePhotoUrl,
    String averageRating,
    Long offeredUserSkillId,
    String offeredSkillName,
    Long requestedUserSkillId,
    String requestedSkillName,
    int totalScore,
    int reciprocityScore,
    int ratingScore,
    int availabilityScore,
    int pointsCost
) {
}

