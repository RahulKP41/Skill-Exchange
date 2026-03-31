package com.skillexchange.api.controller;

import com.skillexchange.api.dto.skill.UserSkillRequest;
import com.skillexchange.api.dto.skill.UserSkillResponse;
import com.skillexchange.api.security.AppUserPrincipal;
import com.skillexchange.api.service.SkillService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-skills")
@RequiredArgsConstructor
public class UserSkillController {

    private final SkillService skillService;

    @GetMapping
    public List<UserSkillResponse> mySkills(@AuthenticationPrincipal AppUserPrincipal principal) {
        return skillService.listUserSkills(principal.getId());
    }

    @PostMapping
    public UserSkillResponse addSkill(
        @AuthenticationPrincipal AppUserPrincipal principal,
        @Valid @RequestBody UserSkillRequest request
    ) {
        return skillService.addUserSkill(principal.getId(), request);
    }

    @DeleteMapping("/{id}")
    public void removeSkill(@AuthenticationPrincipal AppUserPrincipal principal, @PathVariable Long id) {
        skillService.deleteUserSkill(principal.getId(), id);
    }
}

