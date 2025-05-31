package com.project.dadn.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dadn.enums.NotificationType;
import com.project.dadn.enums.PlantStage;
import com.project.dadn.models.Notification;
import com.project.dadn.models.Plant;
import com.project.dadn.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Notification createStageChangeNotification(Plant plant, PlantStage oldStage, PlantStage newStage) {
        Notification notification = Notification.builder()
                .user(plant.getOwner())
                .type(NotificationType.PLANT_STAGE_CHANGE)
                .title("Plant Stage Changed")
                .content(String.format("Your plant '%s' has progressed from %s to %s stage",
                        plant.getName(), oldStage, newStage))
                .actionUrl("/plants/" + plant.getId())
                .build();

        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId);
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public Page<Notification> getUserNotificationsByType(UUID userId,
                                                         NotificationType type,
                                                         Pageable pageable) {
        return notificationRepository.findByUserIdAndType(userId, type, pageable);
    }

    @Transactional
    public void markAsRead(List<UUID> notificationIds, UUID userId) {
        try {
            int updatedCount = notificationRepository.markAsRead(notificationIds, userId);
            log.info("Marked {} notifications as read for user {}", updatedCount, userId);
        } catch (Exception e) {
            log.error("Error marking notifications as read: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to mark notifications as read", e);
        }
    }

    public List<Notification> getUnreadNotifications(UUID userId) {
        try {
            return notificationRepository.findUnreadByUserId(userId);
        } catch (Exception e) {
            log.error("Error fetching unread notifications: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch unread notifications", e);
        }
    }



}
