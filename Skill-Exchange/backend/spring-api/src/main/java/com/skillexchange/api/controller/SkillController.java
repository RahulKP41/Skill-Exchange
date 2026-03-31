package com.skillexchange.api.controller;

import com.skillexchange.api.dto.skill.SkillResponse;
import com.skillexchange.api.service.SkillService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public List<SkillResponse> listSkills() {
        return skillService.listSkills();
    }
}

