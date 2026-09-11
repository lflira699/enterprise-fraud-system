package com.efs.modules.notification.dto;

import java.util.UUID;

public record PreparedNotificationDelivery(
        UUID notificationDeliveryId,
        UUID recipientUserId,
        String channel,
        String destination) {

    public PreparedNotificationDelivery {

        if (notificationDeliveryId == null) {
            throw new IllegalArgumentException(
                    "notificationDeliveryId is required"
            );
        }

        if (recipientUserId == null) {
            throw new IllegalArgumentException(
                    "recipientUserId is required"
            );
        }

        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException(
                    "channel is required"
            );
        }

        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException(
                    "destination is required"
            );
        }
    }
}