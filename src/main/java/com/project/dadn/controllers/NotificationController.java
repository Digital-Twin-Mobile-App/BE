package com.project.dadn.controllers;

import com.project.dadn.dtos.responses.NotificationResponse;
import com.project.dadn.enums.NotificationType;
import com.project.dadn.mappers.NotificationMapper;
import com.project.dadn.models.Notification;
import com.project.dadn.models.User;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.services.NotificationService;
import com.project.dadn.utlls.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @PatchMapping("/mark-read")
    public ResponseEntity<Void> markNotificationsAsRead(
            @RequestBody List<UUID> notificationIds,
            HttpServletRequest request
    ) throws ParseException {
        String email = jwtUtil.getEmailToken(jwtUtil.getUserToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();
        notificationService.markAsRead(notificationIds, user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            HttpServletRequest request
    ) throws ParseException {
        String email = jwtUtil.getEmailToken(jwtUtil.getUserToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(user.getId());
        return ResponseEntity.ok(notificationMapper.toResponseList(unreadNotifications));
    }

    @GetMapping("/count/unread")
    public ResponseEntity<Long> getUnreadCount(HttpServletRequest request) throws ParseException {
        String email = jwtUtil.getEmailToken(jwtUtil.getUserToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(notificationService.getUnreadCount(user.getId()));
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(HttpServletRequest request) throws ParseException {
        String email = jwtUtil.getEmailToken(jwtUtil.getUserToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-type")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByType(
            @RequestParam NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpServletRequest request
    ) throws ParseException {
        String email = jwtUtil.getEmailToken(jwtUtil.getUserToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();
        Page<Notification> notifications = notificationService.getUserNotificationsByType(
                user.getId(), type, PageRequest.of(page, size));
        return ResponseEntity.ok(notifications.map(notificationMapper::toResponse));
    }
}