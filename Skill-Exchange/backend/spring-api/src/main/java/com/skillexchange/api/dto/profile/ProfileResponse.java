package com.skillexchange.api.dto.profile;

import com.skillexchange.api.dto.skill.UserSkillResponse;
import java.util.List;

public record ProfileResponse(
    Long id,
    String fullName,
    String email,
    String headline,
    String bio,
    String phone,
    String location,
    String profilePhotoUrl,
    String role,
    Integer pointsBalance,
    String averageRating,
    Integer totalReviews,
    long unreadNotifications,
    List<AvailabilitySlotResponse> availability,
    List<UserSkillResponse> skills
) {
}

