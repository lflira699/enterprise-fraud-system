package com.efs.modules.notification.service;

import com.efs.modules.notification.dto.NotificationDeliveryPreflightResult;
import com.efs.modules.notification.entity.Notification;
import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import com.efs.modules.notification.event.NotificationRequestedEventMessage;
import com.efs.modules.notification.repository.NotificationRecipientDeliveryRepository;
import com.efs.modules.notification.repository.NotificationRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationDeliveryPreparationService {

    private static final String STATUS_PENDING =
            "PENDING";

    private static final String STATUS_PROCESSING =
            "PROCESSING";

    private final NotificationRepository
            notificationRepository;

    private final NotificationRecipientDeliveryRepository
            notificationRecipientDeliveryRepository;

    private final NotificationDeliveryPreflightService
            notificationDeliveryPreflightService;

    public NotificationDeliveryPreparationService(
            NotificationRepository notificationRepository,
            NotificationRecipientDeliveryRepository
                    notificationRecipientDeliveryRepository,
            NotificationDeliveryPreflightService
                    notificationDeliveryPreflightService) {

        this.notificationRepository =
                notificationRepository;

        this.notificationRecipientDeliveryRepository =
                notificationRecipientDeliveryRepository;

        this.notificationDeliveryPreflightService =
                notificationDeliveryPreflightService;
    }

    @Transactional
    public NotificationDeliveryPreflightResult prepare(
            UUID notificationId,
            NotificationRequestedEventMessage message) {

        if (notificationId == null) {
            throw new IllegalArgumentException(
                    "Notification id is required"
            );
        }

        if (message == null) {
            throw new IllegalArgumentException(
                    "NotificationRequested event message is required"
            );
        }

        Notification notification =
                notificationRepository
                        .findByNotificationIdForUpdate(
                                notificationId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Notification not found: "
                                                        + notificationId
                                        )
                        );

        if (!STATUS_PENDING.equals(
                notification.getNotificationStatus()
        )) {

            throw new IllegalStateException(
                    "Notification must be PENDING for delivery preparation"
            );
        }

        List<NotificationRecipientDelivery> deliveries =
                notificationRecipientDeliveryRepository
                        .findByNotificationIdOrderByCreatedAtAsc(
                                notificationId
                        );

        NotificationDeliveryPreflightResult result =
                notificationDeliveryPreflightService
                        .prepare(
                                notification,
                                deliveries,
                                message
                        );

        notification.setNotificationStatus(
                STATUS_PROCESSING
        );

        notificationRepository.save(
                notification
        );

        return result;
    }
}