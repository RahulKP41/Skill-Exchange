package com.skillexchange.api.service;

import com.skillexchange.api.dto.skill.SkillResponse;
import com.skillexchange.api.dto.skill.UserSkillRequest;
import com.skillexchange.api.dto.skill.UserSkillResponse;
import com.skillexchange.api.entity.Skill;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.entity.UserSkill;
import com.skillexchange.api.enums.ProficiencyLevel;
import com.skillexchange.api.enums.SkillType;
import com.skillexchange.api.exception.ApiException;
import com.skillexchange.api.repository.SkillRepository;
import com.skillexchange.api.repository.UserRepository;
import com.skillexchange.api.repository.UserSkillRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<SkillResponse> listSkills() {
        return skillRepository.findAllByOrderByCategoryAscNameAsc().stream()
            .map(skill -> new SkillResponse(skill.getId(), skill.getName(), skill.getCategory(), skill.getDescription(), skill.getIcon()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<UserSkillResponse> listUserSkills(Long userId) {
        return userSkillRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::mapUserSkill).toList();
    }

    @Transactional
    public UserSkillResponse addUserSkill(Long userId, UserSkillRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found."));
        Skill skill = skillRepository.findById(request.skillId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Skill not found."));
        SkillType skillType = SkillType.valueOf(request.skillType().toUpperCase());
        boolean exists = userSkillRepository.findByUserIdAndSkillType(userId, skillType).stream()
            .anyMatch(existing -> existing.getSkill().getId().equals(skill.getId()));
        if (exists) {
            throw new ApiException(HttpStatus.CONFLICT, "This skill is already added for the selected skill type.");
        }

        UserSkill saved = userSkillRepository.save(UserSkill.builder()
            .user(user)
            .skill(skill)
            .skillType(skillType)
            .proficiencyLevel(ProficiencyLevel.valueOf(request.proficiencyLevel().toUpperCase()))
            .yearsOfExperience(request.yearsOfExperience() == null ? 0 : request.yearsOfExperience())
            .highlights(request.highlights())
            .featured(Boolean.TRUE.equals(request.featured()))
            .build());

        return mapUserSkill(saved);
    }

    @Transactional
    public void deleteUserSkill(Long userId, Long userSkillId) {
        UserSkill userSkill = userSkillRepository.findByIdAndUserId(userSkillId, userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User skill not found."));
        userSkillRepository.delete(userSkill);
    }

    private UserSkillResponse mapUserSkill(UserSkill userSkill) {
        return new UserSkillResponse(
            userSkill.getId(),
            userSkill.getSkill().getId(),
            userSkill.getSkill().getName(),
            userSkill.getSkill().getCategory(),
            userSkill.getSkillType().name(),
            userSkill.getProficiencyLevel().name(),
            userSkill.getYearsOfExperience(),
            userSkill.getHighlights(),
            userSkill.getFeatured()
        );
    }
}

