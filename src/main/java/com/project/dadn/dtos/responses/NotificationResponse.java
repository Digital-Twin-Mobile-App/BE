package com.project.dadn.dtos.responses;

import com.project.dadn.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String title;
    private String content;
    private NotificationType type;
    private String imageUrl;
    private boolean read;
    private String actionUrl;
    private LocalDateTime createdAt;
    private UserBriefResponse user;
}