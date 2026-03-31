package com.skillexchange.api.controller;

import com.skillexchange.api.dto.profile.ProfileResponse;
import com.skillexchange.api.dto.profile.ProfileUpdateRequest;
import com.skillexchange.api.security.AppUserPrincipal;
import com.skillexchange.api.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ProfileResponse getProfile(@AuthenticationPrincipal AppUserPrincipal principal) {
        return profileService.getCurrentProfile(principal.getId());
    }

    @PutMapping
    public ProfileResponse updateProfile(
        @AuthenticationPrincipal AppUserPrincipal principal,
        @RequestBody ProfileUpdateRequest request
    ) {
        return profileService.updateProfile(principal.getId(), request);
    }
}

