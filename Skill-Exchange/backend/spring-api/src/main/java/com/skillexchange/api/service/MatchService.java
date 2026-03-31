package com.skillexchange.api.service;

import com.skillexchange.api.dto.match.MatchResponse;
import com.skillexchange.api.entity.AvailabilitySlot;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.entity.UserSkill;
import com.skillexchange.api.enums.SkillType;
import com.skillexchange.api.repository.AvailabilitySlotRepository;
import com.skillexchange.api.repository.UserRepository;
import com.skillexchange.api.repository.UserSkillRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;

    @Transactional(readOnly = true)
    public List<MatchResponse> findMatches(Long userId) {
        User currentUser = userRepository.findById(userId).orElseThrow();
        List<UserSkill> currentTeach = userSkillRepository.findByUserIdAndSkillType(userId, SkillType.TEACH);
        List<UserSkill> currentLearn = userSkillRepository.findByUserIdAndSkillType(userId, SkillType.LEARN);
        Map<Long, List<UserSkill>> userSkills = new HashMap<>();
        userSkillRepository.findAll().forEach(skill -> userSkills.computeIfAbsent(skill.getUser().getId(), ignored -> new ArrayList<>()).add(skill));
        Map<Long, List<AvailabilitySlot>> availabilityByUser = new HashMap<>();
        availabilitySlotRepository.findAll().forEach(slot -> availabilityByUser.computeIfAbsent(slot.getUser().getId(), ignored -> new ArrayList<>()).add(slot));

        List<MatchResponse> matches = new ArrayList<>();
        for (User candidate : userRepository.findAll()) {
            if (Objects.equals(candidate.getId(), currentUser.getId()) || !Boolean.TRUE.equals(candidate.getActive())) {
                continue;
            }
            List<UserSkill> candidateSkills = userSkills.getOrDefault(candidate.getId(), List.of());
            List<UserSkill> candidateTeach = candidateSkills.stream().filter(skill -> skill.getSkillType() == SkillType.TEACH).toList();
            List<UserSkill> candidateLearn = candidateSkills.stream().filter(skill -> skill.getSkillType() == SkillType.LEARN).toList();

            UserSkill requestedSkill = bestSkillMatch(currentLearn, candidateTeach);
            UserSkill offeredSkill = bestSkillMatch(candidateLearn, currentTeach);
            if (requestedSkill == null || offeredSkill == null) {
                continue;
            }

            int reciprocityScore = 70;
            int ratingScore = normalizeRating(candidate.getAverageRating());
            int availabilityScore = overlapScore(
                availabilityByUser.getOrDefault(currentUser.getId(), List.of()),
                availabilityByUser.getOrDefault(candidate.getId(), List.of())
            );
            int totalScore = reciprocityScore + ratingScore + availabilityScore;

            matches.add(new MatchResponse(
                candidate.getId(),
                candidate.getFullName(),
                candidate.getHeadline(),
                candidate.getLocation(),
                candidate.getProfilePhotoUrl(),
                candidate.getAverageRating().toPlainString(),
                offeredSkill.getId(),
                offeredSkill.getSkill().getName(),
                requestedSkill.getId(),
                requestedSkill.getSkill().getName(),
                totalScore,
                reciprocityScore,
                ratingScore,
                availabilityScore,
                350
            ));
        }

        return matches.stream()
            .sorted(Comparator.comparingInt(MatchResponse::totalScore).reversed())
            .toList();
    }

    private UserSkill bestSkillMatch(List<UserSkill> desiredSkills, List<UserSkill> availableSkills) {
        for (UserSkill desired : desiredSkills) {
            for (UserSkill available : availableSkills) {
                if (Objects.equals(desired.getSkill().getId(), available.getSkill().getId())) {
                    return available;
                }
            }
        }
        return null;
    }

    private int normalizeRating(BigDecimal averageRating) {
        return Math.min(25, averageRating.multiply(BigDecimal.valueOf(5)).intValue());
    }

    private int overlapScore(List<AvailabilitySlot> first, List<AvailabilitySlot> second) {
        for (AvailabilitySlot left : first) {
            for (AvailabilitySlot right : second) {
                if (left.getWeekday().equalsIgnoreCase(right.getWeekday())) {
                    return 5;
                }
            }
        }
        return 0;
    }
}
