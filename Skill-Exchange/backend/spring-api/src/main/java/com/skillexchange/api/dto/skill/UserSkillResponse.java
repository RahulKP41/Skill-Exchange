package com.skillexchange.api.dto.skill;

public record UserSkillResponse(
    Long id,
    Long skillId,
    String skillName,
    String category,
    String skillType,
    String proficiencyLevel,
    Integer yearsOfExperience,
    String highlights,
    Boolean featured
) {
}

