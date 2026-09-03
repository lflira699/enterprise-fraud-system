package com.efs.modules.integration.service;

import com.efs.modules.integration.entity.OutboxEvent;
import com.efs.modules.integration.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class OutboxEventLifecycleService {

    private static final String STATUS_PENDING =
            "PENDING";

    private static final String STATUS_PROCESSING =
            "PROCESSING";

    private static final String STATUS_PUBLISHED =
            "PUBLISHED";

    private static final String STATUS_FAILED =
            "FAILED";

    private static final int MAX_FAILED_ATTEMPTS =
            4;

    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventLifecycleService(
            OutboxEventRepository outboxEventRepository) {

        this.outboxEventRepository =
                outboxEventRepository;
    }

    @Transactional
    public Optional<OutboxEvent> claimForPublication(
            UUID eventId) {

        if (eventId == null) {
            throw new IllegalArgumentException(
                    "Outbox event id is required"
            );
        }

        Optional<OutboxEvent> optionalEvent =
                outboxEventRepository
                        .findByIdForUpdate(eventId);

        if (optionalEvent.isEmpty()) {
            return Optional.empty();
        }

        OutboxEvent event =
                optionalEvent.get();

        if (STATUS_PENDING.equals(
                event.getStatus())) {

            event.setStatus(
                    STATUS_PROCESSING
            );

            outboxEventRepository.save(event);

            return Optional.of(event);
        }

        if (isRetryableFailedEvent(event)) {

            event.setStatus(
                    STATUS_PROCESSING
            );

            outboxEventRepository.save(event);

            return Optional.of(event);
        }

        return Optional.empty();
    }

    @Transactional
    public void markPublished(
            UUID eventId) {

        OutboxEvent event =
                getLockedEvent(eventId);

        requireProcessing(event);

        event.setStatus(
                STATUS_PUBLISHED
        );

        event.setPublishedAt(
                LocalDateTime.now()
        );

        event.setNextAttemptAt(null);
        event.setLastError(null);

        outboxEventRepository.save(event);
    }

    @Transactional
    public void markFailed(
            UUID eventId,
            String errorMessage) {

        if (errorMessage == null
                || errorMessage.isBlank()) {

            throw new IllegalArgumentException(
                    "Outbox publication error is required"
            );
        }

        OutboxEvent event =
                getLockedEvent(eventId);

        requireProcessing(event);

        int failedAttempts =
                event.getAttemptCount() + 1;

        event.setStatus(
                STATUS_FAILED
        );

        event.setAttemptCount(
                failedAttempts
        );

        event.setPublishedAt(null);

        event.setLastError(
                errorMessage
        );

        event.setNextAttemptAt(
                calculateNextAttemptAt(
                        failedAttempts
                )
        );

        outboxEventRepository.save(event);
    }

    private OutboxEvent getLockedEvent(
            UUID eventId) {

        if (eventId == null) {
            throw new IllegalArgumentException(
                    "Outbox event id is required"
            );
        }

        return outboxEventRepository
                .findByIdForUpdate(eventId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Outbox event not found: "
                                        + eventId
                        )
                );
    }

    private boolean isRetryableFailedEvent(
            OutboxEvent event) {

        if (!STATUS_FAILED.equals(
                event.getStatus())) {

            return false;
        }

        Integer attemptCount =
                event.getAttemptCount();

        if (attemptCount == null
                || attemptCount >= MAX_FAILED_ATTEMPTS) {

            return false;
        }

        LocalDateTime nextAttemptAt =
                event.getNextAttemptAt();

        return nextAttemptAt != null
                && !nextAttemptAt.isAfter(
                        LocalDateTime.now()
                );
    }

    private void requireProcessing(
            OutboxEvent event) {

        if (!STATUS_PROCESSING.equals(
                event.getStatus())) {

            throw new IllegalStateException(
                    "Outbox event must be PROCESSING"
            );
        }
    }

    private LocalDateTime calculateNextAttemptAt(
            int failedAttempts) {

        LocalDateTime now =
                LocalDateTime.now();

        return switch (failedAttempts) {

            case 1 ->
                    now.plusSeconds(5);

            case 2 ->
                    now.plusSeconds(15);

            case 3 ->
                    now.plusSeconds(60);

            default ->
                    null;
        };
    }
}