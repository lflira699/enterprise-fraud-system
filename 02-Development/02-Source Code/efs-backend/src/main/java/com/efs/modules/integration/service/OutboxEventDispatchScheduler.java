package com.efs.modules.integration.service;

import com.efs.modules.integration.entity.OutboxEvent;
import com.efs.modules.integration.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class OutboxEventDispatchScheduler {

    private static final String STATUS_PENDING =
            "PENDING";

    private static final String STATUS_PROCESSING =
            "PROCESSING";

    private static final String STATUS_FAILED =
            "FAILED";

    private final OutboxEventRepository
            outboxEventRepository;

    private final OutboxEventLifecycleService
            outboxEventLifecycleService;

    private final OutboxEventPublicationService
            outboxEventPublicationService;

    private final long staleProcessingThresholdMs;

    public OutboxEventDispatchScheduler(
            OutboxEventRepository outboxEventRepository,
            OutboxEventLifecycleService
                    outboxEventLifecycleService,
            OutboxEventPublicationService
                    outboxEventPublicationService,
            long staleProcessingThresholdMs,
            long publisherConfirmTimeoutMs) {

        validateRuntimeConfiguration(
                staleProcessingThresholdMs,
                publisherConfirmTimeoutMs
        );

        this.outboxEventRepository =
                outboxEventRepository;

        this.outboxEventLifecycleService =
                outboxEventLifecycleService;

        this.outboxEventPublicationService =
                outboxEventPublicationService;

        this.staleProcessingThresholdMs =
                staleProcessingThresholdMs;
    }

    public static void validateRuntimeConfiguration(
            long staleProcessingThresholdMs,
            long publisherConfirmTimeoutMs) {

        if (publisherConfirmTimeoutMs <= 0) {
            throw new IllegalArgumentException(
                    "Outbox publisher confirm timeout must be greater than zero"
            );
        }

        if (staleProcessingThresholdMs <= 0) {
            throw new IllegalArgumentException(
                    "Outbox stale processing threshold must be greater than zero"
            );
        }

        if (staleProcessingThresholdMs
                <= publisherConfirmTimeoutMs) {

            throw new IllegalArgumentException(
                    "Outbox stale processing threshold must be greater than publisher confirm timeout"
            );
        }
    }

    @Scheduled(
            fixedDelayString =
                    "${efs.integration.outbox.dispatch.interval-ms:1000}"
    )
    public void dispatch() {

        LocalDateTime dispatchTime =
                LocalDateTime.now();

        LocalDateTime staleCutoff =
                dispatchTime.minus(
                        Duration.ofMillis(
                                staleProcessingThresholdMs
                        )
                );

        List<OutboxEvent> staleProcessingEvents =
                outboxEventRepository
                        .findByStatusAndProcessingStartedAtLessThanEqualOrderByOccurredAtAsc(
                                STATUS_PROCESSING,
                                staleCutoff
                        );

        recoverStaleEvents(
                staleProcessingEvents,
                staleCutoff
        );

        List<OutboxEvent> pendingEvents =
                outboxEventRepository
                        .findByStatusOrderByOccurredAtAsc(
                                STATUS_PENDING
                        );

        dispatchEvents(
                pendingEvents
        );

        List<OutboxEvent> retryableFailedEvents =
                outboxEventRepository
                        .findByStatusAndNextAttemptAtLessThanEqualOrderByOccurredAtAsc(
                                STATUS_FAILED,
                                dispatchTime
                        );

        dispatchEvents(
                retryableFailedEvents
        );
    }

    private void recoverStaleEvents(
            List<OutboxEvent> events,
            LocalDateTime staleCutoff) {

        for (OutboxEvent event : events) {

            try {

                outboxEventLifecycleService
                        .recoverStaleProcessing(
                                event.getId(),
                                staleCutoff
                        );

            } catch (RuntimeException ignored) {

                // One stale recovery failure must not abort the cycle.
            }
        }
    }

    private void dispatchEvents(
            List<OutboxEvent> events) {

        for (OutboxEvent event : events) {

            try {

                outboxEventPublicationService
                        .publish(
                                event.getId()
                        )
                        .join();

            } catch (RuntimeException ignored) {

                // Outbox lifecycle owns FAILED state and retry scheduling.
            }
        }
    }
}