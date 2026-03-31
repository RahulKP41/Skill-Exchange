package com.skillexchange.api.controller;

import com.skillexchange.api.dto.session.CompleteSessionRequest;
import com.skillexchange.api.dto.session.CreateSessionRequest;
import com.skillexchange.api.dto.session.SessionResponse;
import com.skillexchange.api.security.AppUserPrincipal;
import com.skillexchange.api.service.SessionService;
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
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public List<SessionResponse> sessions(@AuthenticationPrincipal AppUserPrincipal principal) {
        return sessionService.listForUser(principal.getId());
    }

    @PostMapping
    public SessionResponse create(
        @AuthenticationPrincipal AppUserPrincipal principal,
        @Valid @RequestBody CreateSessionRequest request
    ) {
        return sessionService.create(principal.getId(), request);
    }

    @PutMapping("/{id}/complete")
    public SessionResponse complete(
        @AuthenticationPrincipal AppUserPrincipal principal,
        @PathVariable Long id,
        @RequestBody CompleteSessionRequest request
    ) {
        return sessionService.complete(principal.getId(), id, request);
    }
}

