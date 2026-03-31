package com.skillexchange.api.dto.skill;

import jakarta.validation.constraints.NotNull;

public record UserSkillRequest(
    @NotNull(message = "Skill id is required")
    Long skillId,
    @NotNull(message = "Skill type is required")
    String skillType,
    @NotNull(message = "Proficiency level is required")
    String proficiencyLevel,
    Integer yearsOfExperience,
    String highlights,
    Boolean featured
) {
}

