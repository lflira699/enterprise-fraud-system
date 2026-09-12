package com.efs.modules.notification.event;

import com.efs.modules.integration.config.RabbitMQConfig;
import com.efs.modules.notification.service.NotificationDeliveryOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationRequestedEventListenerTest {

    private NotificationRequestedEventParser
            eventParser;

    private NotificationDeliveryOrchestrator
            notificationDeliveryOrchestrator;

    private NotificationRequestedEventListener
            listener;

    @BeforeEach
    void setUp() {

        eventParser =
                mock(
                        NotificationRequestedEventParser.class
                );

        notificationDeliveryOrchestrator =
                mock(
                        NotificationDeliveryOrchestrator.class
                );

        listener =
                new NotificationRequestedEventListener(
                        eventParser,
                        notificationDeliveryOrchestrator
                );
    }

    @Test
    void shouldParseAndOrchestrateNotificationRequestedEvent() {

        byte[] body =
                """
                {
                  "eventType": "NotificationRequested"
                }
                """
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        NotificationRequestedEventMessage message =
                createMessage();

        when(
                eventParser.parse(
                        body
                )
        ).thenReturn(
                message
        );

        when(
                notificationDeliveryOrchestrator
                        .orchestrate(
                                message
                        )
        ).thenReturn(
                Optional.empty()
        );

        listener.consume(
                body
        );

        verify(
                eventParser
        ).parse(
                body
        );

        verify(
                notificationDeliveryOrchestrator
        ).orchestrate(
                message
        );
    }

    @Test
    void shouldPropagateParserFailure() {

        byte[] body =
                "invalid"
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        IllegalArgumentException expectedException =
                new IllegalArgumentException(
                        "Invalid NotificationRequested event"
                );

        when(
                eventParser.parse(
                        body
                )
        ).thenThrow(
                expectedException
        );

        IllegalArgumentException actualException =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                listener.consume(
                                        body
                                )
                );

        assertSame(
                expectedException,
                actualException
        );

        verify(
                notificationDeliveryOrchestrator,
                never()
        ).orchestrate(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void shouldPropagateOrchestrationFailure() {

        byte[] body =
                """
                {
                  "eventType": "NotificationRequested"
                }
                """
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        NotificationRequestedEventMessage message =
                createMessage();

        when(
                eventParser.parse(
                        body
                )
        ).thenReturn(
                message
        );

        IllegalStateException expectedException =
                new IllegalStateException(
                        "Notification delivery failed"
                );

        when(
                notificationDeliveryOrchestrator
                        .orchestrate(
                                message
                        )
        ).thenThrow(
                expectedException
        );

        IllegalStateException actualException =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                listener.consume(
                                        body
                                )
                );

        assertSame(
                expectedException,
                actualException
        );
    }

    @Test
    void shouldUseApprovedRabbitListenerContract()
            throws Exception {

        Method method =
                NotificationRequestedEventListener.class
                        .getMethod(
                                "consume",
                                byte[].class
                        );

        RabbitListener annotation =
                method.getAnnotation(
                        RabbitListener.class
                );

        assertArrayEquals(
                new String[]{
                        RabbitMQConfig.NOTIFICATION_REQUESTED_QUEUE
                },
                annotation.queues()
        );

        assertEquals(
                RabbitMQConfig.NOTIFICATION_REQUESTED_LISTENER_CONTAINER_FACTORY,
                annotation.containerFactory()
        );
    }

    private NotificationRequestedEventMessage createMessage() {

        return new NotificationRequestedEventMessage(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "CASE_CREATED",
                "CASE_CREATED",
                "EMAIL",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "CASE",
                "CASE",
                UUID.randomUUID(),
                List.of(
                        UUID.randomUUID()
                ),
                Map.of(
                        "caseNumber",
                        "CASE-1"
                )
        );
    }
}