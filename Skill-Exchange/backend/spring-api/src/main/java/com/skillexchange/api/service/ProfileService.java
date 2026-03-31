package com.skillexchange.api.service;

import com.skillexchange.api.dto.profile.AvailabilitySlotRequest;
import com.skillexchange.api.dto.profile.AvailabilitySlotResponse;
import com.skillexchange.api.dto.profile.ProfileResponse;
import com.skillexchange.api.dto.profile.ProfileUpdateRequest;
import com.skillexchange.api.dto.profile.PublicUserResponse;
import com.skillexchange.api.dto.skill.UserSkillResponse;
import com.skillexchange.api.entity.AvailabilitySlot;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.exception.ApiException;
import com.skillexchange.api.repository.AvailabilitySlotRepository;
import com.skillexchange.api.repository.UserRepository;
import com.skillexchange.api.repository.UserSkillRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillService skillService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public ProfileResponse getCurrentProfile(Long userId) {
        User user = requiredUser(userId);
        List<AvailabilitySlotResponse> availability = availabilitySlotRepository.findByUserIdOrderByWeekdayAscStartTimeAsc(userId).stream()
            .map(this::mapAvailability)
            .toList();
        List<UserSkillResponse> skills = skillService.listUserSkills(userId);
        return new ProfileResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getHeadline(),
            user.getBio(),
            user.getPhone(),
            user.getLocation(),
            user.getProfilePhotoUrl(),
            user.getRole().name(),
            user.getPointsBalance(),
            user.getAverageRating().toPlainString(),
            user.getTotalReviews(),
            notificationService.unreadCount(userId),
            availability,
            skills
        );
    }

    @Transactional(readOnly = true)
    public PublicUserResponse getPublicProfile(Long userId) {
        User user = requiredUser(userId);
        return new PublicUserResponse(
            user.getId(),
            user.getFullName(),
            user.getHeadline(),
            user.getLocation(),
            user.getBio(),
            user.getProfilePhotoUrl(),
            user.getPointsBalance(),
            user.getAverageRating().toPlainString()
        );
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = requiredUser(userId);
        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }
        if (request.headline() != null) {
            user.setHeadline(request.headline());
        }
        if (request.bio() != null) {
            user.setBio(request.bio());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.location() != null) {
            user.setLocation(request.location());
        }
        if (request.profilePhotoUrl() != null) {
            user.setProfilePhotoUrl(request.profilePhotoUrl());
        }
        userRepository.save(user);

        if (request.availability() != null) {
            availabilitySlotRepository.deleteByUserId(userId);
            List<AvailabilitySlot> slots = request.availability().stream()
                .map(slot -> AvailabilitySlot.builder()
                    .user(user)
                    .weekday(slot.weekday().toUpperCase())
                    .startTime(slot.startTime())
                    .endTime(slot.endTime())
                    .timezone(slot.timezone())
                    .build())
                .toList();
            availabilitySlotRepository.saveAll(slots);
        }
        return getCurrentProfile(userId);
    }

    @Transactional(readOnly = true)
    public List<PublicUserResponse> featuredUsers() {
        return userRepository.findAll().stream()
            .filter(user -> Boolean.TRUE.equals(user.getActive()))
            .limit(8)
            .map(user -> new PublicUserResponse(
                user.getId(),
                user.getFullName(),
                user.getHeadline(),
                user.getLocation(),
                user.getBio(),
                user.getProfilePhotoUrl(),
                user.getPointsBalance(),
                user.getAverageRating().toPlainString()
            ))
            .toList();
    }

    private User requiredUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found."));
    }

    private AvailabilitySlotResponse mapAvailability(AvailabilitySlot slot) {
        return new AvailabilitySlotResponse(slot.getId(), slot.getWeekday(), slot.getStartTime(), slot.getEndTime(), slot.getTimezone());
    }
}

