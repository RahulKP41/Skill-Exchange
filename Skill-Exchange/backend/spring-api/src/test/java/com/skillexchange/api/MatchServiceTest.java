package com.skillexchange.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.skillexchange.api.dto.match.MatchResponse;
import com.skillexchange.api.entity.AvailabilitySlot;
import com.skillexchange.api.entity.Skill;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.entity.UserSkill;
import com.skillexchange.api.enums.ProficiencyLevel;
import com.skillexchange.api.enums.Role;
import com.skillexchange.api.enums.SkillType;
import com.skillexchange.api.repository.AvailabilitySlotRepository;
import com.skillexchange.api.repository.UserRepository;
import com.skillexchange.api.repository.UserSkillRepository;
import com.skillexchange.api.service.MatchService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSkillRepository userSkillRepository;

    @Mock
    private AvailabilitySlotRepository availabilitySlotRepository;

    @InjectMocks
    private MatchService matchService;

    @Test
    void returnsReciprocalMatchForCompatibleUsers() {
        Skill javascript = Skill.builder().id(1L).name("JavaScript").category("Development").build();
        Skill speaking = Skill.builder().id(2L).name("Public Speaking").category("Communication").build();

        User current = User.builder().id(1L).fullName("Aarav").active(true).averageRating(BigDecimal.valueOf(4.7)).role(Role.USER).build();
        User candidate = User.builder().id(2L).fullName("Maya").active(true).averageRating(BigDecimal.valueOf(4.9)).role(Role.USER).build();

        UserSkill currentTeach = UserSkill.builder().id(1L).user(current).skill(javascript).skillType(SkillType.TEACH).proficiencyLevel(ProficiencyLevel.ADVANCED).build();
        UserSkill currentLearn = UserSkill.builder().id(2L).user(current).skill(speaking).skillType(SkillType.LEARN).proficiencyLevel(ProficiencyLevel.BEGINNER).build();
        UserSkill candidateTeach = UserSkill.builder().id(3L).user(candidate).skill(speaking).skillType(SkillType.TEACH).proficiencyLevel(ProficiencyLevel.ADVANCED).build();
        UserSkill candidateLearn = UserSkill.builder().id(4L).user(candidate).skill(javascript).skillType(SkillType.LEARN).proficiencyLevel(ProficiencyLevel.INTERMEDIATE).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(current));
        when(userRepository.findAll()).thenReturn(List.of(current, candidate));
        when(userSkillRepository.findByUserIdAndSkillType(1L, SkillType.TEACH)).thenReturn(List.of(currentTeach));
        when(userSkillRepository.findByUserIdAndSkillType(1L, SkillType.LEARN)).thenReturn(List.of(currentLearn));
        when(userSkillRepository.findAll()).thenReturn(List.of(currentTeach, currentLearn, candidateTeach, candidateLearn));
        when(availabilitySlotRepository.findAll()).thenReturn(List.of(
            AvailabilitySlot.builder().user(current).weekday("MONDAY").startTime("18:00").endTime("20:00").timezone("Asia/Kolkata").build(),
            AvailabilitySlot.builder().user(candidate).weekday("MONDAY").startTime("19:00").endTime("21:00").timezone("Asia/Kolkata").build()
        ));

        List<MatchResponse> matches = matchService.findMatches(1L);

        assertEquals(1, matches.size());
        assertEquals("Maya", matches.get(0).fullName());
        assertEquals("JavaScript", matches.get(0).offeredSkillName());
        assertEquals("Public Speaking", matches.get(0).requestedSkillName());
    }
}

