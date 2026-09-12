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

    private static final String STALE_PROCESSING_ERROR =
            "OUTBOX_PROCESSING_LEASE_EXPIRED";

    private static final int MAX_FAILED_ATTEMPTS =
            4;

    private final OutboxEventRepository
            outboxEventRepository;

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

        Optional<OutboxEvent> lockedEvent =
                outboxEventRepository
                        .findByIdForUpdate(
                                eventId
                        );

        if (lockedEvent.isEmpty()) {
            return Optional.empty();
        }

        OutboxEvent event =
                lockedEvent.get();

        if (STATUS_PENDING.equals(
                event.getStatus())) {

            transitionToProcessing(
                    event
            );

            return Optional.of(
                    event
            );
        }

        if (isRetryableFailedEvent(
                event)) {

            transitionToProcessing(
                    event
            );

            return Optional.of(
                    event
            );
        }

        return Optional.empty();
    }

    @Transactional
    public boolean markPublished(
            UUID eventId,
            int expectedAttemptCount) {

        requireExpectedAttemptCount(
                expectedAttemptCount
        );

        OutboxEvent event =
                getLockedEvent(
                        eventId
                );

        if (!isCurrentProcessingAttempt(
                event,
                expectedAttemptCount)) {

            return false;
        }

        event.setStatus(
                STATUS_PUBLISHED
        );

        event.setPublishedAt(
                LocalDateTime.now()
        );

        event.setProcessingStartedAt(
                null
        );

        event.setNextAttemptAt(
                null
        );

        event.setLastError(
                null
        );

        outboxEventRepository.save(
                event
        );

        return true;
    }

    @Transactional
    public boolean markFailed(
            UUID eventId,
            int expectedAttemptCount,
            String errorMessage) {

        if (errorMessage == null
                || errorMessage.isBlank()) {

            throw new IllegalArgumentException(
                    "Outbox publication error is required"
            );
        }

        requireExpectedAttemptCount(
                expectedAttemptCount
        );

        OutboxEvent event =
                getLockedEvent(
                        eventId
                );

        if (!isCurrentProcessingAttempt(
                event,
                expectedAttemptCount)) {

            return false;
        }

        transitionToFailed(
                event,
                errorMessage
        );

        return true;
    }

    @Transactional
    public boolean recoverStaleProcessing(
            UUID eventId,
            LocalDateTime staleCutoff) {

        if (staleCutoff == null) {
            throw new IllegalArgumentException(
                    "Outbox stale processing cutoff is required"
            );
        }

        OutboxEvent event =
                getLockedEvent(
                        eventId
                );

        if (!STATUS_PROCESSING.equals(
                event.getStatus())) {

            return false;
        }

        LocalDateTime processingStartedAt =
                event.getProcessingStartedAt();

        if (processingStartedAt == null
                || processingStartedAt.isAfter(
                        staleCutoff
                )) {

            return false;
        }

        transitionToFailed(
                event,
                STALE_PROCESSING_ERROR
        );

        return true;
    }

    private void transitionToProcessing(
            OutboxEvent event) {

        event.setStatus(
                STATUS_PROCESSING
        );

        event.setProcessingStartedAt(
                LocalDateTime.now()
        );

        event.setNextAttemptAt(
                null
        );

        outboxEventRepository.save(
                event
        );
    }

    private void transitionToFailed(
            OutboxEvent event,
            String errorMessage) {

        Integer currentAttemptCount =
                event.getAttemptCount();

        if (currentAttemptCount == null) {
            throw new IllegalStateException(
                    "Outbox event attempt count is required"
            );
        }

        int failedAttempts =
                currentAttemptCount + 1;

        event.setStatus(
                STATUS_FAILED
        );

        event.setAttemptCount(
                failedAttempts
        );

        event.setProcessingStartedAt(
                null
        );

        event.setPublishedAt(
                null
        );

        event.setLastError(
                errorMessage
        );

        event.setNextAttemptAt(
                calculateNextAttemptAt(
                        failedAttempts
                )
        );

        outboxEventRepository.save(
                event
        );
    }

    private OutboxEvent getLockedEvent(
            UUID eventId) {

        if (eventId == null) {
            throw new IllegalArgumentException(
                    "Outbox event id is required"
            );
        }

        return outboxEventRepository
                .findByIdForUpdate(
                        eventId
                )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Outbox event not found: "
                                                + eventId
                                )
                );
    }

    private boolean isCurrentProcessingAttempt(
            OutboxEvent event,
            int expectedAttemptCount) {

        if (!STATUS_PROCESSING.equals(
                event.getStatus())) {

            return false;
        }

        Integer actualAttemptCount =
                event.getAttemptCount();

        return actualAttemptCount != null
                && actualAttemptCount
                == expectedAttemptCount;
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

    private void requireExpectedAttemptCount(
            int expectedAttemptCount) {

        if (expectedAttemptCount < 0) {
            throw new IllegalArgumentException(
                    "Outbox expected attempt count must not be negative"
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