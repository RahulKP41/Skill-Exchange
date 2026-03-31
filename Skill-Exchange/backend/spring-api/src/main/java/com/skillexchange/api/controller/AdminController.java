package com.skillexchange.api.controller;

import com.skillexchange.api.dto.admin.AdminOverviewResponse;
import com.skillexchange.api.dto.admin.ModerationReportResponse;
import com.skillexchange.api.dto.admin.ReportStatusUpdateRequest;
import com.skillexchange.api.security.AppUserPrincipal;
import com.skillexchange.api.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/overview")
    public AdminOverviewResponse overview(@AuthenticationPrincipal AppUserPrincipal principal) {
        return adminService.overview(principal.getId());
    }

    @GetMapping("/reports")
    public List<ModerationReportResponse> reports() {
        return adminService.reports();
    }

    @PutMapping("/reports/{id}")
    public ModerationReportResponse updateReport(
        @AuthenticationPrincipal AppUserPrincipal principal,
        @PathVariable Long id,
        @Valid @RequestBody ReportStatusUpdateRequest request
    ) {
        return adminService.updateReport(principal.getId(), id, request.status());
    }
}

