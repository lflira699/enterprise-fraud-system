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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class OutboxEventRabbitPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final DomainEventRoutingKeyResolver routingKeyResolver;
    private final long publisherConfirmTimeoutMs;

    public OutboxEventRabbitPublisher(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper,
            DomainEventRoutingKeyResolver routingKeyResolver,
            @Value(
                    "${efs.integration.outbox.publisher-confirm-timeout-ms:5000}"
            )
            long publisherConfirmTimeoutMs) {

        if (publisherConfirmTimeoutMs <= 0) {
            throw new IllegalArgumentException(
                    "Outbox publisher confirm timeout must be greater than zero"
            );
        }

        this.rabbitTemplate =
                rabbitTemplate;

        this.objectMapper =
                objectMapper;

        this.routingKeyResolver =
                routingKeyResolver;

        this.publisherConfirmTimeoutMs =
                publisherConfirmTimeoutMs;
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
                    .orTimeout(
                            publisherConfirmTimeoutMs,
                            TimeUnit.MILLISECONDS
                    )
                    .thenCompose(confirm -> {

                        if (correlationData.getReturned() != null) {

                            return CompletableFuture.<Void>failedFuture(
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

                            return CompletableFuture.<Void>failedFuture(
                                    new IllegalStateException(
                                            reason == null
                                                    ? "RabbitMQ publisher NACK"
                                                    : "RabbitMQ publisher NACK: "
                                                    + reason
                                    )
                            );
                        }

                        return CompletableFuture.<Void>completedFuture(
                                null
                        );
                    })
                    .exceptionallyCompose(
                            throwable -> {

                                Throwable cause =
                                        unwrap(
                                                throwable
                                        );

                                if (cause
                                        instanceof TimeoutException) {

                                    return CompletableFuture
                                            .failedFuture(
                                                    new IllegalStateException(
                                                            "RabbitMQ publisher confirm timeout after "
                                                                    + publisherConfirmTimeoutMs
                                                                    + " ms",
                                                            cause
                                                    )
                                            );
                                }

                                return CompletableFuture
                                        .failedFuture(
                                                cause
                                        );
                            }
                    );

        } catch (Exception exception) {

            return CompletableFuture.<Void>failedFuture(
                    exception
            );
        }
    }

    private Throwable unwrap(
            Throwable throwable) {

        if (throwable
                instanceof CompletionException
                && throwable.getCause() != null) {

            return throwable.getCause();
        }

        return throwable;
    }
}