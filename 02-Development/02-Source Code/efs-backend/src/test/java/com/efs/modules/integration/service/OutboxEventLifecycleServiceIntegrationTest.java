package com.efs.modules.integration.service;

import com.efs.modules.integration.entity.OutboxEvent;
import com.efs.modules.integration.repository.OutboxEventRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class OutboxEventLifecycleServiceIntegrationTest {

    @Autowired
    private OutboxEventLifecycleService lifecycleService;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldClaimPendingEventForPublication() {

        OutboxEvent saved =
                outboxEventRepository.saveAndFlush(
                        createEvent(
                                "PENDING",
                                0
                        )
                );

        Optional<OutboxEvent> claimed =
                lifecycleService.claimForPublication(
                        saved.getId()
                );

        assertTrue(claimed.isPresent());

        entityManager.flush();
        entityManager.clear();

        OutboxEvent persisted =
                outboxEventRepository
                        .findById(saved.getId())
                        .orElseThrow();

        assertEquals(
                "PROCESSING",
                persisted.getStatus()
        );

        assertEquals(
                Integer.valueOf(0),
                persisted.getAttemptCount()
        );
    }

    @Test
    void shouldClaimEligibleFailedEventForRetry() {

        OutboxEvent event =
                createEvent(
                        "FAILED",
                        1
                );

        event.setNextAttemptAt(
                LocalDateTime.now()
                        .minusSeconds(1)
        );

        OutboxEvent saved =
                outboxEventRepository.saveAndFlush(
                        event
                );

        Optional<OutboxEvent> claimed =
                lifecycleService.claimForPublication(
                        saved.getId()
                );

        assertTrue(claimed.isPresent());

        entityManager.flush();
        entityManager.clear();

        OutboxEvent persisted =
                outboxEventRepository
                        .findById(saved.getId())
                        .orElseThrow();

        assertEquals(
                "PROCESSING",
                persisted.getStatus()
        );

        assertEquals(
                Integer.valueOf(1),
                persisted.getAttemptCount()
        );
    }

    @Test
    void shouldNotClaimFailedEventBeforeRetryTime() {

        OutboxEvent event =
                createEvent(
                        "FAILED",
                        1
                );

        event.setNextAttemptAt(
                LocalDateTime.now()
                        .plusMinutes(1)
        );

        OutboxEvent saved =
                outboxEventRepository.saveAndFlush(
                        event
                );

        Optional<OutboxEvent> claimed =
                lifecycleService.claimForPublication(
                        saved.getId()
                );

        assertTrue(claimed.isEmpty());

        assertEquals(
                "FAILED",
                saved.getStatus()
        );
    }

    @Test
    void shouldNotClaimPermanentlyFailedEvent() {

        OutboxEvent event =
                createEvent(
                        "FAILED",
                        4
                );

        event.setNextAttemptAt(null);

        OutboxEvent saved =
                outboxEventRepository.saveAndFlush(
                        event
                );

        Optional<OutboxEvent> claimed =
                lifecycleService.claimForPublication(
                        saved.getId()
                );

        assertTrue(claimed.isEmpty());

        assertEquals(
                "FAILED",
                saved.getStatus()
        );

        assertEquals(
                Integer.valueOf(4),
                saved.getAttemptCount()
        );

        assertNull(
                saved.getNextAttemptAt()
        );
    }

    @Test
    void shouldMarkProcessingEventAsPublished() {

        OutboxEvent event =
                createEvent(
                        "PROCESSING",
                        2
                );

        event.setNextAttemptAt(
                LocalDateTime.now()
                        .minusSeconds(1)
        );

        event.setLastError(
                "previous-error"
        );

        OutboxEvent saved =
                outboxEventRepository.saveAndFlush(
                        event
                );

        lifecycleService.markPublished(
                saved.getId()
        );

        entityManager.flush();
        entityManager.clear();

        OutboxEvent persisted =
                outboxEventRepository
                        .findById(saved.getId())
                        .orElseThrow();

        assertEquals(
                "PUBLISHED",
                persisted.getStatus()
        );

        assertEquals(
                Integer.valueOf(2),
                persisted.getAttemptCount()
        );

        assertNotNull(
                persisted.getPublishedAt()
        );

        assertNull(
                persisted.getNextAttemptAt()
        );

        assertNull(
                persisted.getLastError()
        );
    }

    @Test
    void shouldScheduleFiveSecondRetryAfterFirstFailure() {

        verifyFailureTransition(
                0,
                1,
                5
        );
    }

    @Test
    void shouldScheduleFifteenSecondRetryAfterSecondFailure() {

        verifyFailureTransition(
                1,
                2,
                15
        );
    }

    @Test
    void shouldScheduleSixtySecondRetryAfterThirdFailure() {

        verifyFailureTransition(
                2,
                3,
                60
        );
    }

    @Test
    void shouldRemainFailedPermanentlyAfterFourthFailure() {

        OutboxEvent saved =
                outboxEventRepository.saveAndFlush(
                        createEvent(
                                "PROCESSING",
                                3
                        )
                );

        lifecycleService.markFailed(
                saved.getId(),
                "fourth-failure"
        );

        entityManager.flush();
        entityManager.clear();

        OutboxEvent persisted =
                outboxEventRepository
                        .findById(saved.getId())
                        .orElseThrow();

        assertEquals(
                "FAILED",
                persisted.getStatus()
        );

        assertEquals(
                Integer.valueOf(4),
                persisted.getAttemptCount()
        );

        assertEquals(
                "fourth-failure",
                persisted.getLastError()
        );

        assertNull(
                persisted.getNextAttemptAt()
        );

        assertNull(
                persisted.getPublishedAt()
        );
    }

    @Test
    void shouldReturnEmptyWhenClaimingUnknownEvent() {

        Optional<OutboxEvent> claimed =
                lifecycleService.claimForPublication(
                        UUID.randomUUID()
                );

        assertFalse(
                claimed.isPresent()
        );
    }

    private void verifyFailureTransition(
            int currentAttemptCount,
            int expectedAttemptCount,
            long expectedDelaySeconds) {

        OutboxEvent saved =
                outboxEventRepository.saveAndFlush(
                        createEvent(
                                "PROCESSING",
                                currentAttemptCount
                        )
                );

        LocalDateTime earliestExpected =
                LocalDateTime.now()
                        .plusSeconds(
                                expectedDelaySeconds
                        );

        lifecycleService.markFailed(
                saved.getId(),
                "publication-failure"
        );

        LocalDateTime latestExpected =
                LocalDateTime.now()
                        .plusSeconds(
                                expectedDelaySeconds
                        );

        entityManager.flush();
        entityManager.clear();

        OutboxEvent persisted =
                outboxEventRepository
                        .findById(saved.getId())
                        .orElseThrow();

        assertEquals(
                "FAILED",
                persisted.getStatus()
        );

        assertEquals(
                Integer.valueOf(
                        expectedAttemptCount
                ),
                persisted.getAttemptCount()
        );

        assertEquals(
                "publication-failure",
                persisted.getLastError()
        );

        assertNull(
                persisted.getPublishedAt()
        );

        assertNotNull(
                persisted.getNextAttemptAt()
        );

        assertFalse(
                persisted
                        .getNextAttemptAt()
                        .isBefore(
                                earliestExpected
                        )
        );

        assertFalse(
                persisted
                        .getNextAttemptAt()
                        .isAfter(
                                latestExpected
                        )
        );
    }

    private OutboxEvent createEvent(
            String status,
            int attemptCount) {

        OutboxEvent event =
                new OutboxEvent();

        event.setAggregateType(
                "INTEGRATION_TEST"
        );

        event.setAggregateId(
                UUID.randomUUID()
        );

        event.setEventType(
                "DecisionGenerated"
        );

        event.setPayload(
                Map.of(
                        "eventType",
                        "DecisionGenerated"
                )
        );

        event.setOccurredAt(
                LocalDateTime.now()
        );

        event.setStatus(
                status
        );

        event.setAttemptCount(
                attemptCount
        );

        return event;
    }
}