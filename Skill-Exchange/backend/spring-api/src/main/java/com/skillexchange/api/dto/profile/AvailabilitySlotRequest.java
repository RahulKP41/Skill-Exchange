package com.skillexchange.api.dto.profile;

import jakarta.validation.constraints.NotBlank;

public record AvailabilitySlotRequest(
    @NotBlank(message = "Weekday is required")
    String weekday,
    @NotBlank(message = "Start time is required")
    String startTime,
    @NotBlank(message = "End time is required")
    String endTime,
    @NotBlank(message = "Timezone is required")
    String timezone
) {
}

