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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class OutboxStaleProcessingRecoveryIntegrationTest {

    @Autowired
    private OutboxEventLifecycleService lifecycleService;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldRecoverExpiredProcessingLeaseAsFailed() {

        OutboxEvent saved =
                saveProcessingEvent(
                        0,
                        LocalDateTime.now()
                                .minusSeconds(
                                        60
                                )
                );

        boolean recovered =
                lifecycleService
                        .recoverStaleProcessing(
                                saved.getId(),
                                LocalDateTime.now()
                                        .minusSeconds(
                                                30
                                        )
                        );

        assertTrue(
                recovered
        );

        OutboxEvent persisted =
                reload(
                        saved
                );

        assertEquals(
                "FAILED",
                persisted.getStatus()
        );

        assertEquals(
                Integer.valueOf(1),
                persisted.getAttemptCount()
        );

        assertEquals(
                "OUTBOX_PROCESSING_LEASE_EXPIRED",
                persisted.getLastError()
        );

        assertNull(
                persisted.getProcessingStartedAt()
        );

        assertNull(
                persisted.getPublishedAt()
        );

        assertNotNull(
                persisted.getNextAttemptAt()
        );
    }

    @Test
    void shouldNotRecoverActiveProcessingLease() {

        OutboxEvent saved =
                saveProcessingEvent(
                        0,
                        LocalDateTime.now()
                                .minusSeconds(
                                        5
                                )
                );

        boolean recovered =
                lifecycleService
                        .recoverStaleProcessing(
                                saved.getId(),
                                LocalDateTime.now()
                                        .minusSeconds(
                                                30
                                        )
                        );

        assertFalse(
                recovered
        );

        OutboxEvent persisted =
                reload(
                        saved
                );

        assertEquals(
                "PROCESSING",
                persisted.getStatus()
        );

        assertEquals(
                Integer.valueOf(0),
                persisted.getAttemptCount()
        );

        assertNotNull(
                persisted.getProcessingStartedAt()
        );
    }

    @Test
    void shouldNotRecoverProcessingEventWithoutLease() {

        OutboxEvent saved =
                saveProcessingEvent(
                        0,
                        null
                );

        boolean recovered =
                lifecycleService
                        .recoverStaleProcessing(
                                saved.getId(),
                                LocalDateTime.now()
                                        .minusSeconds(
                                                30
                                        )
                        );

        assertFalse(
                recovered
        );

        OutboxEvent persisted =
                reload(
                        saved
                );

        assertEquals(
                "PROCESSING",
                persisted.getStatus()
        );

        assertNull(
                persisted.getProcessingStartedAt()
        );
    }

    @Test
    void shouldIgnoreLatePublishedResultFromPreviousAttempt() {

        OutboxEvent saved =
                saveProcessingEvent(
                        1,
                        LocalDateTime.now()
                );

        boolean published =
                lifecycleService
                        .markPublished(
                                saved.getId(),
                                0
                        );

        assertFalse(
                published
        );

        OutboxEvent persisted =
                reload(
                        saved
                );

        assertEquals(
                "PROCESSING",
                persisted.getStatus()
        );

        assertEquals(
                Integer.valueOf(1),
                persisted.getAttemptCount()
        );

        assertNull(
                persisted.getPublishedAt()
        );

        assertNotNull(
                persisted.getProcessingStartedAt()
        );
    }

    @Test
    void shouldIgnoreLateFailedResultFromPreviousAttempt() {

        OutboxEvent saved =
                saveProcessingEvent(
                        2,
                        LocalDateTime.now()
                );

        boolean failed =
                lifecycleService
                        .markFailed(
                                saved.getId(),
                                1,
                                "late-attempt-failure"
                        );

        assertFalse(
                failed
        );

        OutboxEvent persisted =
                reload(
                        saved
                );

        assertEquals(
                "PROCESSING",
                persisted.getStatus()
        );

        assertEquals(
                Integer.valueOf(2),
                persisted.getAttemptCount()
        );

        assertNull(
                persisted.getLastError()
        );

        assertNotNull(
                persisted.getProcessingStartedAt()
        );
    }

    private OutboxEvent saveProcessingEvent(
            int attemptCount,
            LocalDateTime processingStartedAt) {

        OutboxEvent event =
                new OutboxEvent();

        event.setAggregateType(
                "STALE_RECOVERY_TEST"
        );

        event.setAggregateId(
                java.util.UUID.randomUUID()
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
                "PROCESSING"
        );

        event.setAttemptCount(
                attemptCount
        );

        event.setProcessingStartedAt(
                processingStartedAt
        );

        return outboxEventRepository
                .saveAndFlush(
                        event
                );
    }

    private OutboxEvent reload(
            OutboxEvent event) {

        entityManager.flush();
        entityManager.clear();

        return outboxEventRepository
                .findById(
                        event.getId()
                )
                .orElseThrow();
    }
}