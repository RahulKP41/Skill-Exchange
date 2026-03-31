package com.skillexchange.api.dto.profile;

public record AvailabilitySlotResponse(
    Long id,
    String weekday,
    String startTime,
    String endTime,
    String timezone
) {
}

