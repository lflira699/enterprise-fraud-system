package com.efs.modules.integration.service;

import com.efs.modules.integration.config.RabbitMQConfig;
import com.efs.modules.integration.entity.OutboxEvent;
import com.efs.modules.integration.event.DomainEventRoutingKeyResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OutboxEventRabbitPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final DomainEventRoutingKeyResolver routingKeyResolver;

    public OutboxEventRabbitPublisher(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper,
            DomainEventRoutingKeyResolver routingKeyResolver) {

        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.routingKeyResolver = routingKeyResolver;
    }

    public CompletableFuture<Void> publish(
            OutboxEvent outboxEvent) {

        try {

            if (outboxEvent == null) {
                throw new IllegalArgumentException(
                        "Outbox event is required"
                );
            }

            if (outboxEvent.getId() == null) {
                throw new IllegalArgumentException(
                        "Outbox event messageId is required"
                );
            }

            if (outboxEvent.getPayload() == null) {
                throw new IllegalArgumentException(
                        "Outbox event payload is required"
                );
            }

            String routingKey =
                    routingKeyResolver.resolve(
                            outboxEvent.getEventType()
                    );

            byte[] body =
                    objectMapper.writeValueAsBytes(
                            outboxEvent.getPayload()
                    );

            Message message =
                    MessageBuilder
                            .withBody(body)
                            .setContentType(
                                    MessageProperties.CONTENT_TYPE_JSON
                            )
                            .build();

            CorrelationData correlationData =
                    new CorrelationData(
                            outboxEvent
                                    .getId()
                                    .toString()
                    );

            rabbitTemplate.send(
                    RabbitMQConfig.DOMAIN_EVENTS_EXCHANGE,
                    routingKey,
                    message,
                    correlationData
            );

            return correlationData
                    .getFuture()
                    .thenCompose(confirm -> {

                        if (correlationData.getReturned() != null) {

                            return CompletableFuture.failedFuture(
                                    new IllegalStateException(
                                            "RabbitMQ message returned as unroutable: "
                                                    + correlationData
                                                    .getReturned()
                                                    .getReplyText()
                                    )
                            );
                        }

                        if (!confirm.isAck()) {

                            String reason =
                                    confirm.getReason();

                            return CompletableFuture.failedFuture(
                                    new IllegalStateException(
                                            reason == null
                                                    ? "RabbitMQ publisher NACK"
                                                    : "RabbitMQ publisher NACK: "
                                                    + reason
                                    )
                            );
                        }

                        return CompletableFuture.completedFuture(
                                null
                        );
                    });

        } catch (Exception exception) {

            return CompletableFuture.failedFuture(
                    exception
            );
        }
    }
}