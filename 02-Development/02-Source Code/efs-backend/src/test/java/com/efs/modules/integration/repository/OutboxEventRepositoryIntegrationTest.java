package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.OutboxEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OutboxEventRepositoryIntegrationTest {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Test
    void shouldSaveAndFindOutboxEventById() {

        OutboxEvent event =
                createEvent(
                        "PENDING",
                        LocalDateTime.now()
                );

        OutboxEvent saved =
                outboxEventRepository.saveAndFlush(event);

        assertNotNull(saved.getId());

        OutboxEvent found =
                outboxEventRepository
                        .findById(saved.getId())
                        .orElseThrow();

        assertEquals(
                event.getAggregateType(),
                found.getAggregateType()
        );

        assertEquals(
                event.getAggregateId(),
                found.getAggregateId()
        );

        assertEquals(
                event.getEventType(),
                found.getEventType()
        );

        assertEquals(
                "PENDING",
                found.getStatus()
        );

        assertEquals(
                Integer.valueOf(0),
                found.getAttemptCount()
        );

        assertNotNull(found.getOccurredAt());
    }

    @Test
    void shouldPersistPayloadAndOptionalFields() {

        OutboxEvent event =
                createEvent(
                        "FAILED",
                        LocalDateTime.now()
                );

        UUID correlationId =
                UUID.randomUUID();

        LocalDateTime nextAttemptAt =
                LocalDateTime.now()
                        .plusMinutes(5);

        LocalDateTime publishedAt =
                LocalDateTime.now();

        event.setCorrelationId(correlationId);
        event.setAttemptCount(2);
        event.setNextAttemptAt(nextAttemptAt);
        event.setPublishedAt(publishedAt);
        event.setLastError(
                "Integration test error"
        );

        event.setPayload(
                Map.of(
                        "source",
                        "integration-test",
                        "attempt",
                        2
                )
        );

        OutboxEvent saved =
                outboxEventRepository.saveAndFlush(event);

        OutboxEvent found =
                outboxEventRepository
                        .findById(saved.getId())
                        .orElseThrow();

        assertEquals(
                correlationId,
                found.getCorrelationId()
        );

        assertEquals(
                Integer.valueOf(2),
                found.getAttemptCount()
        );

        assertNotNull(found.getNextAttemptAt());
        assertNotNull(found.getPublishedAt());

        assertEquals(
                "Integration test error",
                found.getLastError()
        );

        assertNotNull(found.getPayload());

        assertEquals(
                "integration-test",
                found.getPayload().get("source")
        );

        assertEquals(
                2,
                ((Number) found
                        .getPayload()
                        .get("attempt"))
                        .intValue()
        );
    }

    @Test
    void shouldFindEventsByStatusOrderedByOccurredAtAscending() {

        LocalDateTime now =
                LocalDateTime.now();

        OutboxEvent older =
                outboxEventRepository.saveAndFlush(
                        createEvent(
                                "PROCESSING",
                                now.minusMinutes(10)
                        )
                );

        OutboxEvent newer =
                outboxEventRepository.saveAndFlush(
                        createEvent(
                                "PROCESSING",
                                now
                        )
                );

        List<OutboxEvent> results =
                outboxEventRepository
                        .findByStatusOrderByOccurredAtAsc(
                                "PROCESSING"
                        );

        int olderIndex =
                indexOfEvent(
                        results,
                        older.getId()
                );

        int newerIndex =
                indexOfEvent(
                        results,
                        newer.getId()
                );

        assertTrue(olderIndex >= 0);
        assertTrue(newerIndex >= 0);
        assertTrue(olderIndex < newerIndex);
    }

    @Test
    void shouldFindRetryableEventsByStatusAndNextAttemptAt() {

        LocalDateTime now =
                LocalDateTime.now();

        OutboxEvent eligible =
                createEvent(
                        "FAILED",
                        now.minusMinutes(10)
                );

        eligible.setNextAttemptAt(
                now.minusMinutes(1)
        );

        OutboxEvent future =
                createEvent(
                        "FAILED",
                        now.minusMinutes(5)
                );

        future.setNextAttemptAt(
                now.plusMinutes(30)
        );

        OutboxEvent differentStatus =
                createEvent(
                        "PENDING",
                        now.minusMinutes(15)
                );

        differentStatus.setNextAttemptAt(
                now.minusMinutes(2)
        );

        OutboxEvent savedEligible =
                outboxEventRepository.saveAndFlush(
                        eligible
                );

        OutboxEvent savedFuture =
                outboxEventRepository.saveAndFlush(
                        future
                );

        OutboxEvent savedDifferentStatus =
                outboxEventRepository.saveAndFlush(
                        differentStatus
                );

        List<OutboxEvent> results =
                outboxEventRepository
                        .findByStatusAndNextAttemptAtLessThanEqualOrderByOccurredAtAsc(
                                "FAILED",
                                now
                        );

        assertTrue(
                containsEvent(
                        results,
                        savedEligible.getId()
                )
        );

        assertFalse(
                containsEvent(
                        results,
                        savedFuture.getId()
                )
        );

        assertFalse(
                containsEvent(
                        results,
                        savedDifferentStatus.getId()
                )
        );
    }

    @Test
    void shouldAllowNullableOptionalFields() {

        OutboxEvent event =
                createEvent(
                        "PENDING",
                        LocalDateTime.now()
                );

        event.setCorrelationId(null);
        event.setNextAttemptAt(null);
        event.setPublishedAt(null);
        event.setLastError(null);

        OutboxEvent saved =
                outboxEventRepository.saveAndFlush(event);

        OutboxEvent found =
                outboxEventRepository
                        .findById(saved.getId())
                        .orElseThrow();

        assertNull(found.getCorrelationId());
        assertNull(found.getNextAttemptAt());
        assertNull(found.getPublishedAt());
        assertNull(found.getLastError());
    }

    @Test
    void shouldReturnEmptyForUnknownStatus() {

        String unknownStatus =
                "UNKNOWN-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        List<OutboxEvent> results =
                outboxEventRepository
                        .findByStatusOrderByOccurredAtAsc(
                                unknownStatus
                        );

        assertTrue(results.isEmpty());
    }

    private OutboxEvent createEvent(
            String status,
            LocalDateTime occurredAt) {

        OutboxEvent event =
                new OutboxEvent();

        event.setAggregateType(
                "TRANSACTION"
        );

        event.setAggregateId(
                UUID.randomUUID()
        );

        event.setEventType(
                "V113_INTEGRATION_TEST"
        );

        event.setPayload(
                Map.of(
                        "source",
                        "integration-test"
                )
        );

        event.setOccurredAt(
                occurredAt
        );

        event.setStatus(
                status
        );

        event.setAttemptCount(
                0
        );

        return event;
    }

    private boolean containsEvent(
            List<OutboxEvent> events,
            UUID eventId) {

        return indexOfEvent(
                events,
                eventId
        ) >= 0;
    }

    private int indexOfEvent(
            List<OutboxEvent> events,
            UUID eventId) {

        for (int i = 0;
             i < events.size();
             i++) {

            if (eventId.equals(
                    events.get(i).getId())) {

                return i;
            }
        }

        return -1;
    }
}