package com.efs.modules.integration.service;

import com.efs.modules.integration.config.RabbitMQConfig;
import com.efs.modules.integration.entity.OutboxEvent;
import com.efs.modules.integration.event.DomainEventRoutingKeyResolver;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class OutboxEventRabbitPublisherTest {

    private RabbitTemplate rabbitTemplate;
    private ObjectMapper objectMapper;
    private OutboxEventRabbitPublisher publisher;

    @BeforeEach
    void setUp() {

        rabbitTemplate = mock(RabbitTemplate.class);

        objectMapper = new ObjectMapper();

        publisher =
                new OutboxEventRabbitPublisher(
                        rabbitTemplate,
                        objectMapper,
                        new DomainEventRoutingKeyResolver()
                );
    }

    @Test
    void shouldPublishStoredEnvelopeAndCompleteAfterAck()
            throws Exception {

        UUID messageId = UUID.randomUUID();

        Map<String, Object> envelope =
                new LinkedHashMap<>();

        envelope.put(
                "messageId",
                messageId.toString()
        );

        envelope.put(
                "eventType",
                "DecisionGenerated"
        );

        envelope.put(
                "schemaVersion",
                "1.0"
        );

        Map<String, Object> businessPayload =
                new LinkedHashMap<>();

        businessPayload.put(
                "decisionId",
                UUID.randomUUID().toString()
        );

        envelope.put(
                "payload",
                businessPayload
        );

        OutboxEvent outboxEvent =
                createOutboxEvent(
                        messageId,
                        "DecisionGenerated",
                        envelope
                );

        var messageCaptor =
                org.mockito.ArgumentCaptor
                        .forClass(Message.class);

        var correlationCaptor =
                org.mockito.ArgumentCaptor
                        .forClass(CorrelationData.class);

        CompletableFuture<Void> result =
                publisher.publish(outboxEvent);

        verify(rabbitTemplate)
                .send(
                        eq(
                                RabbitMQConfig
                                        .DOMAIN_EVENTS_EXCHANGE
                        ),
                        eq("decision.generated.v1"),
                        messageCaptor.capture(),
                        correlationCaptor.capture()
                );

        CorrelationData correlationData =
                correlationCaptor.getValue();

        assertEquals(
                messageId.toString(),
                correlationData.getId()
        );

        Map<String, Object> publishedEnvelope =
                objectMapper.readValue(
                        messageCaptor
                                .getValue()
                                .getBody(),
                        new TypeReference<>() {
                        }
                );

        assertEquals(
                envelope,
                publishedEnvelope
        );

        correlationData
                .getFuture()
                .complete(
                        new CorrelationData.Confirm(
                                true,
                                null
                        )
                );

        result.join();

        assertTrue(
                result.isDone()
        );
    }

    @Test
    void shouldFailWhenBrokerNacksMessage() {

        UUID messageId = UUID.randomUUID();

        OutboxEvent outboxEvent =
                createOutboxEvent(
                        messageId,
                        "RiskCalculated",
                        Map.of(
                                "messageId",
                                messageId.toString(),
                                "eventType",
                                "RiskCalculated"
                        )
                );

        var correlationCaptor =
                org.mockito.ArgumentCaptor
                        .forClass(CorrelationData.class);

        CompletableFuture<Void> result =
                publisher.publish(outboxEvent);

        verify(rabbitTemplate)
                .send(
                        eq(
                                RabbitMQConfig
                                        .DOMAIN_EVENTS_EXCHANGE
                        ),
                        eq("risk.calculated.v1"),
                        org.mockito.ArgumentMatchers
                                .any(Message.class),
                        correlationCaptor.capture()
                );

        correlationCaptor
                .getValue()
                .getFuture()
                .complete(
                        new CorrelationData.Confirm(
                                false,
                                "broker-nack"
                        )
                );

        CompletionException exception =
                assertThrows(
                        CompletionException.class,
                        result::join
                );

        assertTrue(
                exception
                        .getCause()
                        .getMessage()
                        .contains("broker-nack")
        );
    }

    @Test
    void shouldFailWhenMessageIsReturnedAsUnroutable() {

        UUID messageId = UUID.randomUUID();

        OutboxEvent outboxEvent =
                createOutboxEvent(
                        messageId,
                        "AlertCreated",
                        Map.of(
                                "messageId",
                                messageId.toString(),
                                "eventType",
                                "AlertCreated"
                        )
                );

        var correlationCaptor =
                org.mockito.ArgumentCaptor
                        .forClass(CorrelationData.class);

        CompletableFuture<Void> result =
                publisher.publish(outboxEvent);

        verify(rabbitTemplate)
                .send(
                        eq(
                                RabbitMQConfig
                                        .DOMAIN_EVENTS_EXCHANGE
                        ),
                        eq("alert.created.v1"),
                        org.mockito.ArgumentMatchers
                                .any(Message.class),
                        correlationCaptor.capture()
                );

        CorrelationData correlationData =
                correlationCaptor.getValue();

        Message returnedMessage =
                MessageBuilder
                        .withBody(new byte[0])
                        .build();

        correlationData.setReturned(
                new ReturnedMessage(
                        returnedMessage,
                        312,
                        "NO_ROUTE",
                        RabbitMQConfig
                                .DOMAIN_EVENTS_EXCHANGE,
                        "alert.created.v1"
                )
        );

        correlationData
                .getFuture()
                .complete(
                        new CorrelationData.Confirm(
                                true,
                                null
                        )
                );

        CompletionException exception =
                assertThrows(
                        CompletionException.class,
                        result::join
                );

        assertTrue(
                exception
                        .getCause()
                        .getMessage()
                        .contains("NO_ROUTE")
        );
    }

    @Test
    void shouldRejectUnsupportedEventWithoutPublishing() {

        UUID messageId = UUID.randomUUID();

        OutboxEvent outboxEvent =
                createOutboxEvent(
                        messageId,
                        "UNKNOWN_EVENT",
                        Map.of(
                                "messageId",
                                messageId.toString()
                        )
                );

        CompletableFuture<Void> result =
                publisher.publish(outboxEvent);

        CompletionException exception =
                assertThrows(
                        CompletionException.class,
                        result::join
                );

        assertTrue(
                exception
                        .getCause()
                        .getMessage()
                        .contains(
                                "Unsupported domain event type"
                        )
        );

        verify(
                rabbitTemplate,
                never()
        ).send(
                org.mockito.ArgumentMatchers
                        .anyString(),
                org.mockito.ArgumentMatchers
                        .anyString(),
                org.mockito.ArgumentMatchers
                        .any(Message.class),
                org.mockito.ArgumentMatchers
                        .any(CorrelationData.class)
        );
    }

    @Test
    void shouldRejectMissingOutboxEvent() {

        CompletableFuture<Void> result =
                publisher.publish(null);

        CompletionException exception =
                assertThrows(
                        CompletionException.class,
                        result::join
                );

        assertEquals(
                "Outbox event is required",
                exception
                        .getCause()
                        .getMessage()
        );
    }

    private OutboxEvent createOutboxEvent(
            UUID messageId,
            String eventType,
            Map<String, Object> payload) {

        OutboxEvent outboxEvent =
                new OutboxEvent();

        outboxEvent.setId(messageId);
        outboxEvent.setEventType(eventType);
        outboxEvent.setPayload(payload);

        return outboxEvent;
    }
}