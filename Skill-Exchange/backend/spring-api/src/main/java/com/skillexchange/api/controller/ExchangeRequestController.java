package com.skillexchange.api.controller;

import com.skillexchange.api.dto.request.CreateExchangeRequestRequest;
import com.skillexchange.api.dto.request.ExchangeRequestResponse;
import com.skillexchange.api.dto.request.RequestStatusUpdateRequest;
import com.skillexchange.api.security.AppUserPrincipal;
import com.skillexchange.api.service.ExchangeRequestService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class ExchangeRequestController {

    private final ExchangeRequestService exchangeRequestService;

    @GetMapping
    public List<ExchangeRequestResponse> myRequests(@AuthenticationPrincipal AppUserPrincipal principal) {
        return exchangeRequestService.listForUser(principal.getId());
    }

    @PostMapping
    public ExchangeRequestResponse create(
        @AuthenticationPrincipal AppUserPrincipal principal,
        @Valid @RequestBody CreateExchangeRequestRequest request
    ) {
        return exchangeRequestService.create(principal.getId(), request);
    }

    @PutMapping("/{id}/status")
    public ExchangeRequestResponse updateStatus(
        @AuthenticationPrincipal AppUserPrincipal principal,
        @PathVariable Long id,
        @Valid @RequestBody RequestStatusUpdateRequest request
    ) {
        return exchangeRequestService.updateStatus(principal.getId(), id, request.status());
    }
}

