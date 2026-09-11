package com.efs.modules.notification.repository;

import com.efs.modules.notification.entity.Notification;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            """
            SELECT notification
            FROM Notification notification
            WHERE notification.notificationId = :notificationId
            """
    )
    Optional<Notification> findByNotificationIdForUpdate(
            @Param("notificationId")
            UUID notificationId
    );
}