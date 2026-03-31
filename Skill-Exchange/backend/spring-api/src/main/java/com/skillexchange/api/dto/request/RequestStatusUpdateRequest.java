package com.skillexchange.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RequestStatusUpdateRequest(@NotBlank(message = "Status is required") String status) {
}

