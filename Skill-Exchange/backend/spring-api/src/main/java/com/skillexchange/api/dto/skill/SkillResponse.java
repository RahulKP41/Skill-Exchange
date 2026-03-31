package com.skillexchange.api.dto.skill;

public record SkillResponse(
    Long id,
    String name,
    String category,
    String description,
    String icon
) {
}

