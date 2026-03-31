package com.skillexchange.api.dto.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFeedbackRequest(
    @NotNull(message = "Session id is required")
    Long sessionId,
    @NotNull(message = "Reviewee id is required")
    Long revieweeId,
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    Integer rating,
    @NotBlank(message = "Comment is required")
    String comment
) {
}

