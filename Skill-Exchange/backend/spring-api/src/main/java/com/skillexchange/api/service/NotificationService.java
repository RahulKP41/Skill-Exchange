package com.skillexchange.api.service;

import com.skillexchange.api.entity.Notification;
import com.skillexchange.api.entity.User;
import com.skillexchange.api.enums.NotificationType;
import com.skillexchange.api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(User user, String title, String message, NotificationType type) {
        notificationRepository.save(Notification.builder()
            .user(user)
            .title(title)
            .message(message)
            .type(type)
            .read(false)
            .build());
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }
}

