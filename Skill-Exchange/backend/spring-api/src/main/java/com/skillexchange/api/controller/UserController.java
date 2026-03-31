package com.skillexchange.api.controller;

import com.skillexchange.api.dto.profile.PublicUserResponse;
import com.skillexchange.api.service.ProfileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ProfileService profileService;

    @GetMapping("/featured")
    public List<PublicUserResponse> featuredUsers() {
        return profileService.featuredUsers();
    }

    @GetMapping("/{id}")
    public PublicUserResponse publicProfile(@PathVariable Long id) {
        return profileService.getPublicProfile(id);
    }
}

