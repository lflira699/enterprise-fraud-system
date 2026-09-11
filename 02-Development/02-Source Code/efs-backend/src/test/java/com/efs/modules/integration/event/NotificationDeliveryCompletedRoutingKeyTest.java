package com.efs.modules.integration.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationDeliveryCompletedRoutingKeyTest {

    @Test
    void shouldResolveApprovedNotificationDeliveryCompletedRoutingKey() {

        DomainEventRoutingKeyResolver resolver =
                new DomainEventRoutingKeyResolver();

        assertEquals(
                "notification.delivery.completed.v1",
                resolver.resolve(
                        "NotificationDeliveryCompleted"
                )
        );
    }
}