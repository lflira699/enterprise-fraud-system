package com.efs.modules.integration.service;

import com.efs.modules.integration.entity.OutboxEvent;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class OutboxEventPublicationService {

    private final OutboxEventLifecycleService lifecycleService;
    private final OutboxEventRabbitPublisher rabbitPublisher;

    public OutboxEventPublicationService(
            OutboxEventLifecycleService lifecycleService,
            OutboxEventRabbitPublisher rabbitPublisher) {

        this.lifecycleService = lifecycleService;
        this.rabbitPublisher = rabbitPublisher;
    }

    public CompletableFuture<Void> publish(
            UUID eventId) {

        try {

            Optional<OutboxEvent> claimedEvent =
                    lifecycleService
                            .claimForPublication(eventId);

            if (claimedEvent.isEmpty()) {
                return CompletableFuture
                        .completedFuture(null);
            }

            OutboxEvent event =
                    claimedEvent.get();

            return rabbitPublisher
                    .publish(event)
                    .handle(
                            (ignored, throwable) -> {

                                if (throwable == null) {

                                    lifecycleService
                                            .markPublished(
                                                    eventId
                                            );

                                    return null;
                                }

                                Throwable cause =
                                        unwrap(throwable);

                                lifecycleService
                                        .markFailed(
                                                eventId,
                                                errorMessage(
                                                        cause
                                                )
                                        );

                                throw new CompletionException(
                                        cause
                                );
                            }
                    );

        } catch (Exception exception) {

            return CompletableFuture
                    .failedFuture(exception);
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

    private String errorMessage(
            Throwable throwable) {

        String message =
                throwable.getMessage();

        if (message != null
                && !message.isBlank()) {

            return message;
        }

        String type =
                throwable
                        .getClass()
                        .getSimpleName();

        if (type != null
                && !type.isBlank()) {

            return type;
        }

        return "Outbox publication failed";
    }
}