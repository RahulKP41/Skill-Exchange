package com.skillexchange.api.controller;

import com.skillexchange.api.dto.match.MatchResponse;
import com.skillexchange.api.security.AppUserPrincipal;
import com.skillexchange.api.service.MatchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public List<MatchResponse> matches(@AuthenticationPrincipal AppUserPrincipal principal) {
        return matchService.findMatches(principal.getId());
    }
}

