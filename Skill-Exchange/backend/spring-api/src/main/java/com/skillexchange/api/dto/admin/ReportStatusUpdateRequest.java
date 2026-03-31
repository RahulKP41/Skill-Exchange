package com.skillexchange.api.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record ReportStatusUpdateRequest(@NotBlank(message = "Status is required") String status) {
}

