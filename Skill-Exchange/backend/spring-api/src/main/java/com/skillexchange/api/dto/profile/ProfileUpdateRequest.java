package com.skillexchange.api.dto.profile;

import java.util.List;

public record ProfileUpdateRequest(
    String fullName,
    String headline,
    String bio,
    String phone,
    String location,
    String profilePhotoUrl,
    List<AvailabilitySlotRequest> availability
) {
}

