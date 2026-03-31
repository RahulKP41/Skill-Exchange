package com.skillexchange.api.controller;

import com.skillexchange.api.dto.feedback.CreateFeedbackRequest;
import com.skillexchange.api.dto.feedback.FeedbackResponse;
import com.skillexchange.api.security.AppUserPrincipal;
import com.skillexchange.api.service.FeedbackService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public List<FeedbackResponse> myFeedback(@AuthenticationPrincipal AppUserPrincipal principal) {
        return feedbackService.listForUser(principal.getId());
    }

    @PostMapping
    public FeedbackResponse create(
        @AuthenticationPrincipal AppUserPrincipal principal,
        @Valid @RequestBody CreateFeedbackRequest request
    ) {
        return feedbackService.create(principal.getId(), request);
    }
}

