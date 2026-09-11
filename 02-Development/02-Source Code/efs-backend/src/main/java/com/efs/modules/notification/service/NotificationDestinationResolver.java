package com.efs.modules.notification.service;

import com.efs.modules.administration.dto.UserAccountReference;
import org.springframework.stereotype.Component;

@Component
public class NotificationDestinationResolver {

    private static final String EMAIL_CHANNEL =
            "EMAIL";

    public String resolve(
            String channel,
            UserAccountReference recipient) {

        if (recipient == null) {
            throw new IllegalArgumentException(
                    "Notification recipient is required"
            );
        }

        if (channel == null
                || channel.isBlank()) {

            throw new IllegalArgumentException(
                    "Notification channel is required"
            );
        }

        if (!EMAIL_CHANNEL.equals(
                channel
        )) {

            throw new IllegalArgumentException(
                    "Unsupported notification destination channel: "
                            + channel
            );
        }

        String email =
                recipient.email();

        if (email == null
                || email.isBlank()) {

            throw new IllegalArgumentException(
                    "Notification email destination is unavailable"
            );
        }

        return email;
    }
}