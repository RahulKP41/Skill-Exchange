package com.skillexchange.api.repository;

import com.skillexchange.api.entity.AvailabilitySlot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByUserIdOrderByWeekdayAscStartTimeAsc(Long userId);
    void deleteByUserId(Long userId);
}

