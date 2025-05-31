package com.project.dadn.repositories;

import com.project.dadn.enums.NotificationType;
import com.project.dadn.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.read = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id IN :ids AND n.user.id = :userId")
    int markAsRead(@Param("ids") List<UUID> notificationIds, @Param("userId") UUID userId);

    long countByUserIdAndReadFalse(UUID userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdAndType(@Param("userId") UUID userId,
                                           @Param("type") NotificationType type,
                                           Pageable pageable);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.id = :userId")
    int markAllAsRead(@Param("userId") UUID userId);
}