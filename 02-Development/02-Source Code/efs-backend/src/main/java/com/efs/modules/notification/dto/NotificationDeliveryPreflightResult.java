package com.efs.modules.notification.dto;

import java.util.List;
import java.util.UUID;

public record NotificationDeliveryPreflightResult(
        UUID notificationId,
        UUID organizationId,
        UUID tenantId,
        UUID correlationId,
        String subject,
        String body,
        List<PreparedNotificationDelivery> deliveries) {

    public NotificationDeliveryPreflightResult {

        if (notificationId == null) {
            throw new IllegalArgumentException(
                    "notificationId is required"
            );
        }

        if (organizationId == null) {
            throw new IllegalArgumentException(
                    "organizationId is required"
            );
        }

        if (tenantId == null) {
            throw new IllegalArgumentException(
                    "tenantId is required"
            );
        }

        if (correlationId == null) {
            throw new IllegalArgumentException(
                    "correlationId is required"
            );
        }

        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException(
                    "body is required"
            );
        }

        if (deliveries == null || deliveries.isEmpty()) {
            throw new IllegalArgumentException(
                    "deliveries are required"
            );
        }

        deliveries =
                List.copyOf(deliveries);
    }
}