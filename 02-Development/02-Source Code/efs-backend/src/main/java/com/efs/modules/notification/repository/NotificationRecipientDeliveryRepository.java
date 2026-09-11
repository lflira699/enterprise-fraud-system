package com.efs.modules.notification.repository;

import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRecipientDeliveryRepository
        extends JpaRepository<
                NotificationRecipientDelivery,
                UUID> {

    List<NotificationRecipientDelivery>
            findByNotificationIdOrderByCreatedAtAsc(
                    UUID notificationId
            );
}
