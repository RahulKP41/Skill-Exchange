package com.skillexchange.api.repository;

import com.skillexchange.api.entity.UserSkill;
import com.skillexchange.api.enums.SkillType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<UserSkill> findByUserIdAndSkillType(Long userId, SkillType skillType);
    List<UserSkill> findBySkillIdAndSkillType(Long skillId, SkillType skillType);
    Optional<UserSkill> findByIdAndUserId(Long id, Long userId);
}
