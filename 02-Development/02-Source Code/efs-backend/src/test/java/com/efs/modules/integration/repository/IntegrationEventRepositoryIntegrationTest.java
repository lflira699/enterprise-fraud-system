package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class IntegrationEventRepositoryIntegrationTest {

    @Autowired
    private IntegrationEventRepository eventRepository;

    @Test
    void shouldSaveIntegrationEvent() {

        LocalDateTime publishedAt =
                LocalDateTime.now();

        IntegrationEvent event =
                createEvent(
                        "TRANSACTION_UPDATED",
                        "1.0",
                        Map.of(
                                "transactionReference",
                                "TX-V87-001"
                        ),
                        publishedAt,
                        "EFS",
                        "PUBLISHED"
                );

        IntegrationEvent saved =
                eventRepository.saveAndFlush(
                        event
                );

        assertNotNull(
                saved.getEventId()
        );

        assertEquals(
                "TRANSACTION_UPDATED",
                saved.getEventName()
        );

        assertEquals(
                "1.0",
                saved.getEventVersion()
        );

        assertEquals(
                "TX-V87-001",
                saved.getEventPayload()
                        .get("transactionReference")
        );

        assertEquals(
                publishedAt,
                saved.getPublishedAt()
        );

        assertEquals(
                "EFS",
                saved.getPublisher()
        );

        assertEquals(
                "PUBLISHED",
                saved.getStatus()
        );
    }

    @Test
    void shouldFindIntegrationEventById() {

        IntegrationEvent saved =
                eventRepository.saveAndFlush(
                        createEvent(
                                "CASE_CREATED",
                                "1.0",
                                Map.of(
                                        "caseReference",
                                        "CASE-V87-001"
                                ),
                                LocalDateTime.now(),
                                "EFS",
                                "PUBLISHED"
                        )
                );

        Optional<IntegrationEvent> result =
                eventRepository.findById(
                        saved.getEventId()
                );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                saved.getEventId(),
                result.get().getEventId()
        );

        assertEquals(
                "CASE_CREATED",
                result.get().getEventName()
        );

        assertEquals(
                "EFS",
                result.get().getPublisher()
        );
    }

    @Test
    void shouldFindEventsByEventNameOrderedByPublishedAtDesc() {

        LocalDateTime baseTime =
                LocalDateTime.now();

        eventRepository.save(
                createEvent(
                        "TRANSACTION_UPDATED",
                        "1.0",
                        Map.of(
                                "sequence",
                                1
                        ),
                        baseTime.minusMinutes(2),
                        "EFS",
                        "PUBLISHED"
                )
        );

        eventRepository.save(
                createEvent(
                        "TRANSACTION_UPDATED",
                        "1.1",
                        Map.of(
                                "sequence",
                                2
                        ),
                        baseTime,
                        "EFS",
                        "PUBLISHED"
                )
        );

        eventRepository.save(
                createEvent(
                        "CASE_CREATED",
                        "1.0",
                        Map.of(
                                "sequence",
                                3
                        ),
                        baseTime.plusMinutes(1),
                        "EFS",
                        "PUBLISHED"
                )
        );

        eventRepository.flush();

        List<IntegrationEvent> result =
                eventRepository
                        .findByEventNameOrderByPublishedAtDesc(
                                "TRANSACTION_UPDATED"
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                "1.1",
                result.get(0).getEventVersion()
        );

        assertEquals(
                "1.0",
                result.get(1).getEventVersion()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                event ->
                                        "TRANSACTION_UPDATED".equals(
                                                event.getEventName()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnEmptyListWhenEventNameDoesNotExist() {

        eventRepository.saveAndFlush(
                createEvent(
                        "TRANSACTION_CREATED",
                        "1.0",
                        Map.of(
                                "test",
                                "V87"
                        ),
                        LocalDateTime.now(),
                        "EFS",
                        "PUBLISHED"
                )
        );

        List<IntegrationEvent> result =
                eventRepository
                        .findByEventNameOrderByPublishedAtDesc(
                                "UNKNOWN_EVENT"
                        );

        assertNotNull(
                result
        );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void shouldFindEventsByStatusOrderedByPublishedAtDesc() {

        LocalDateTime baseTime =
                LocalDateTime.now();

        eventRepository.save(
                createEvent(
                        "OLDER_PUBLISHED_EVENT",
                        "1.0",
                        Map.of(
                                "sequence",
                                1
                        ),
                        baseTime.minusMinutes(2),
                        "EFS",
                        "PUBLISHED"
                )
        );

        eventRepository.save(
                createEvent(
                        "NEWER_PUBLISHED_EVENT",
                        "1.0",
                        Map.of(
                                "sequence",
                                2
                        ),
                        baseTime,
                        "EFS",
                        "PUBLISHED"
                )
        );

        eventRepository.save(
                createEvent(
                        "FAILED_EVENT",
                        "1.0",
                        Map.of(
                                "sequence",
                                3
                        ),
                        baseTime.plusMinutes(1),
                        "EFS",
                        "FAILED"
                )
        );

        eventRepository.flush();

        List<IntegrationEvent> result =
                eventRepository
                        .findByStatusOrderByPublishedAtDesc(
                                "PUBLISHED"
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                "NEWER_PUBLISHED_EVENT",
                result.get(0).getEventName()
        );

        assertEquals(
                "OLDER_PUBLISHED_EVENT",
                result.get(1).getEventName()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                event ->
                                        "PUBLISHED".equals(
                                                event.getStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnEmptyListWhenStatusDoesNotExist() {

        eventRepository.saveAndFlush(
                createEvent(
                        "ACTIVE_EVENT",
                        "1.0",
                        Map.of(
                                "test",
                                "V87"
                        ),
                        LocalDateTime.now(),
                        "EFS",
                        "PUBLISHED"
                )
        );

        List<IntegrationEvent> result =
                eventRepository
                        .findByStatusOrderByPublishedAtDesc(
                                "UNKNOWN"
                        );

        assertNotNull(
                result
        );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void shouldPersistJsonEventPayload() {

        IntegrationEvent event =
                createEvent(
                        "RISK_ASSESSMENT_COMPLETED",
                        "2.0",
                        Map.of(
                                "transactionReference",
                                "TX-V87-JSON",
                                "riskScore",
                                85,
                                "riskLevel",
                                "HIGH"
                        ),
                        LocalDateTime.now(),
                        "EFS",
                        "PUBLISHED"
                );

        IntegrationEvent saved =
                eventRepository.saveAndFlush(
                        event
                );

        assertNotNull(
                saved.getEventPayload()
        );

        assertEquals(
                "TX-V87-JSON",
                saved.getEventPayload()
                        .get("transactionReference")
        );

        assertEquals(
                85,
                ((Number) saved.getEventPayload()
                        .get("riskScore")).intValue()
        );

        assertEquals(
                "HIGH",
                saved.getEventPayload()
                        .get("riskLevel")
        );
    }

    private IntegrationEvent createEvent(
            String eventName,
            String eventVersion,
            Map<String, Object> eventPayload,
            LocalDateTime publishedAt,
            String publisher,
            String status) {

        IntegrationEvent event =
                new IntegrationEvent();

        event.setEventName(
                eventName
        );

        event.setEventVersion(
                eventVersion
        );

        event.setEventPayload(
                eventPayload
        );

        event.setPublishedAt(
                publishedAt
        );

        event.setPublisher(
                publisher
        );

        event.setStatus(
                status
        );

        return event;
    }
}