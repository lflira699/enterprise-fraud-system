package com.efs.modules.notification.service;

public class NotificationDeliveryPreparationException
        extends RuntimeException {

    private final String reason;

    public NotificationDeliveryPreparationException(
            String reason) {

        super(requireReason(reason));

        this.reason =
                reason;
    }

    public String getReason() {
        return reason;
    }

    private static String requireReason(
            String reason) {

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Preparation failure reason is required"
            );
        }

        return reason;
    }
}