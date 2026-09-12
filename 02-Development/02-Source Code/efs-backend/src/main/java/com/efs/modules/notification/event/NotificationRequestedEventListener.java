package com.efs.modules.notification.event;

import com.efs.modules.integration.config.RabbitMQConfig;
import com.efs.modules.notification.service.NotificationDeliveryOrchestrator;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationRequestedEventListener {

    private final NotificationRequestedEventParser
            eventParser;

    private final NotificationDeliveryOrchestrator
            notificationDeliveryOrchestrator;

    public NotificationRequestedEventListener(
            NotificationRequestedEventParser eventParser,
            NotificationDeliveryOrchestrator
                    notificationDeliveryOrchestrator) {

        this.eventParser =
                eventParser;

        this.notificationDeliveryOrchestrator =
                notificationDeliveryOrchestrator;
    }

    @RabbitListener(
            queues =
                    RabbitMQConfig.NOTIFICATION_REQUESTED_QUEUE,
            containerFactory =
                    RabbitMQConfig.NOTIFICATION_REQUESTED_LISTENER_CONTAINER_FACTORY
    )
    public void consume(
            byte[] body) {

        NotificationRequestedEventMessage message =
                eventParser.parse(
                        body
                );

        notificationDeliveryOrchestrator
                .orchestrate(
                        message
                );
    }
}