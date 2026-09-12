package com.efs.modules.integration.service;

import com.efs.modules.integration.entity.OutboxEvent;
import com.efs.modules.integration.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

public class OutboxEventDispatchScheduler {

    private static final String STATUS_PENDING =
            "PENDING";

    private static final String STATUS_FAILED =
            "FAILED";

    private final OutboxEventRepository
            outboxEventRepository;

    private final OutboxEventPublicationService
            outboxEventPublicationService;

    public OutboxEventDispatchScheduler(
            OutboxEventRepository outboxEventRepository,
            OutboxEventPublicationService
                    outboxEventPublicationService) {

        this.outboxEventRepository =
                outboxEventRepository;

        this.outboxEventPublicationService =
                outboxEventPublicationService;
    }

    @Scheduled(
            fixedDelayString =
                    "${efs.integration.outbox.dispatch.interval-ms:1000}"
    )
    public void dispatch() {

        LocalDateTime dispatchTime =
                LocalDateTime.now();

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