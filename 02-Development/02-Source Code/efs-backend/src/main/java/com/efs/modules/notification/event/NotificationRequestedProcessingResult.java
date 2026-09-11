package com.efs.modules.notification.event;

import java.util.UUID;

public record NotificationRequestedProcessingResult(
        Status status,
        UUID notificationId) {

    public enum Status {
        READY,
        TERMINAL,
        DUPLICATE
    }

    public NotificationRequestedProcessingResult {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Notification processing status is required"
            );
        }

        if ((status == Status.READY
                || status == Status.TERMINAL)
                && notificationId == null) {

            throw new IllegalArgumentException(
                    "Notification id is required for "
                            + status
            );
        }

        if (status == Status.DUPLICATE
                && notificationId != null) {

            throw new IllegalArgumentException(
                    "Duplicate notification processing result "
                            + "must not contain notification id"
            );
        }
    }

    public static NotificationRequestedProcessingResult ready(
            UUID notificationId) {

        return new NotificationRequestedProcessingResult(
                Status.READY,
                notificationId
        );
    }

    public static NotificationRequestedProcessingResult terminal(
            UUID notificationId) {

        return new NotificationRequestedProcessingResult(
                Status.TERMINAL,
                notificationId
        );
    }

    public static NotificationRequestedProcessingResult duplicate() {

        return new NotificationRequestedProcessingResult(
                Status.DUPLICATE,
                null
        );
    }
}