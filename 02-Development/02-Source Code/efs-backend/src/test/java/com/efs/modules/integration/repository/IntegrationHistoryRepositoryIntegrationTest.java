package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class IntegrationHistoryRepositoryIntegrationTest {

    @Autowired
    private IntegrationHistoryRepository repository;

    @Test
    void shouldSaveIntegrationHistory() {

        UUID messageId = UUID.randomUUID();
        UUID connectorId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        LocalDateTime originalCreatedAt =
                LocalDateTime.now().minusMinutes(5);

        LocalDateTime archivedAt =
                LocalDateTime.now();

        IntegrationHistory history =
                createHistory(
                        messageId,
                        connectorId,
                        correlationId,
                        requestId,
                        "REQUEST",
                        "EFS",
                        "BANK_CORE",
                        Map.of(
                                "transactionId",
                                "V93-TEST-001"
                        ),
                        125,
                        "PROCESSED",
                        originalCreatedAt,
                        archivedAt
                );

        IntegrationHistory saved =
                repository.saveAndFlush(history);

        assertNotNull(saved.getHistoryId());

        assertEquals(
                messageId,
                saved.getMessageId()
        );

        assertEquals(
                connectorId,
                saved.getConnectorId()
        );

        assertEquals(
                correlationId,
                saved.getCorrelationId()
        );

        assertEquals(
                requestId,
                saved.getRequestId()
        );

        assertEquals(
                "REQUEST",
                saved.getMessageType()
        );

        assertEquals(
                "EFS",
                saved.getSourceSystem()
        );

        assertEquals(
                "BANK_CORE",
                saved.getTargetSystem()
        );

        assertEquals(
                "V93-TEST-001",
                saved.getPayloadJson()
                        .get("transactionId")
        );

        assertEquals(
                125,
                saved.getProcessingTimeMs()
        );

        assertEquals(
                "PROCESSED",
                saved.getMessageStatus()
        );

        assertEquals(
                originalCreatedAt,
                saved.getOriginalCreatedAt()
        );

        assertEquals(
                archivedAt,
                saved.getArchivedAt()
        );
    }

    @Test
    void shouldFindIntegrationHistoryById() {

        IntegrationHistory saved =
                repository.saveAndFlush(
                        createHistory(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "RESPONSE",
                                "BANK_CORE",
                                "EFS",
                                Map.of(
                                        "result",
                                        "APPROVED"
                                ),
                                80,
                                "PROCESSED",
                                LocalDateTime.now()
                                        .minusMinutes(3),
                                LocalDateTime.now()
                        )
                );

        IntegrationHistory result =
                repository
                        .findById(saved.getHistoryId())
                        .orElseThrow();

        assertEquals(
                saved.getHistoryId(),
                result.getHistoryId()
        );

        assertEquals(
                saved.getMessageId(),
                result.getMessageId()
        );

        assertEquals(
                "RESPONSE",
                result.getMessageType()
        );

        assertEquals(
                "APPROVED",
                result.getPayloadJson()
                        .get("result")
        );
    }

    @Test
    void shouldFindHistoryByMessageIdOrderedByArchivedAtDesc() {

        UUID messageId =
                UUID.randomUUID();

        LocalDateTime baseTime =
                LocalDateTime.now();

        repository.save(
                createHistory(
                        messageId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "REQUEST",
                        "EFS",
                        "TARGET_A",
                        Map.of("version", 1),
                        100,
                        "PROCESSED",
                        baseTime.minusMinutes(20),
                        baseTime.minusMinutes(10)
                )
        );

        repository.save(
                createHistory(
                        messageId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "REQUEST",
                        "EFS",
                        "TARGET_A",
                        Map.of("version", 2),
                        110,
                        "PROCESSED",
                        baseTime.minusMinutes(15),
                        baseTime
                )
        );

        repository.flush();

        List<IntegrationHistory> result =
                repository
                        .findByMessageIdOrderByArchivedAtDesc(
                                messageId
                        );

        assertEquals(2, result.size());

        assertEquals(
                2,
                result.get(0)
                        .getPayloadJson()
                        .get("version")
        );

        assertEquals(
                1,
                result.get(1)
                        .getPayloadJson()
                        .get("version")
        );

        assertTrue(
                result.get(0)
                        .getArchivedAt()
                        .isAfter(
                                result.get(1)
                                        .getArchivedAt()
                        )
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                history ->
                                        messageId.equals(
                                                history.getMessageId()
                                        )
                        )
        );
    }

    @Test
    void shouldFindHistoryByCorrelationIdOrderedByArchivedAtDesc() {

        UUID correlationId =
                UUID.randomUUID();

        LocalDateTime baseTime =
                LocalDateTime.now();

        repository.save(
                createHistory(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        correlationId,
                        UUID.randomUUID(),
                        "REQUEST",
                        "SYSTEM_A",
                        "SYSTEM_B",
                        Map.of("sequence", 1),
                        50,
                        "PROCESSED",
                        baseTime.minusMinutes(30),
                        baseTime.minusMinutes(5)
                )
        );

        repository.save(
                createHistory(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        correlationId,
                        UUID.randomUUID(),
                        "RESPONSE",
                        "SYSTEM_B",
                        "SYSTEM_A",
                        Map.of("sequence", 2),
                        60,
                        "PROCESSED",
                        baseTime.minusMinutes(25),
                        baseTime
                )
        );

        repository.flush();

        List<IntegrationHistory> result =
                repository
                        .findByCorrelationIdOrderByArchivedAtDesc(
                                correlationId
                        );

        assertEquals(2, result.size());

        assertEquals(
                2,
                result.get(0)
                        .getPayloadJson()
                        .get("sequence")
        );

        assertEquals(
                1,
                result.get(1)
                        .getPayloadJson()
                        .get("sequence")
        );

        assertTrue(
                result.get(0)
                        .getArchivedAt()
                        .isAfter(
                                result.get(1)
                                        .getArchivedAt()
                        )
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                history ->
                                        correlationId.equals(
                                                history.getCorrelationId()
                                        )
                        )
        );
    }

    @Test
    void shouldPersistNullableProcessingTime() {

        IntegrationHistory saved =
                repository.saveAndFlush(
                        createHistory(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "EVENT",
                                "EFS",
                                "EXTERNAL_SYSTEM",
                                Map.of(
                                        "event",
                                        "V93_NULLABLE_TEST"
                                ),
                                null,
                                "ARCHIVED",
                                LocalDateTime.now()
                                        .minusMinutes(1),
                                LocalDateTime.now()
                        )
                );

        assertNotNull(saved.getHistoryId());

        assertEquals(
                null,
                saved.getProcessingTimeMs()
        );

        assertEquals(
                "ARCHIVED",
                saved.getMessageStatus()
        );
    }

    @Test
    void shouldReturnEmptyListWhenMessageIdDoesNotExist() {

        List<IntegrationHistory> result =
                repository
                        .findByMessageIdOrderByArchivedAtDesc(
                                UUID.fromString(
                                        "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                                )
                        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenCorrelationIdDoesNotExist() {

        List<IntegrationHistory> result =
                repository
                        .findByCorrelationIdOrderByArchivedAtDesc(
                                UUID.fromString(
                                        "11111111-2222-3333-4444-555555555555"
                                )
                        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private IntegrationHistory createHistory(
            UUID messageId,
            UUID connectorId,
            UUID correlationId,
            UUID requestId,
            String messageType,
            String sourceSystem,
            String targetSystem,
            Map<String, Object> payloadJson,
            Integer processingTimeMs,
            String messageStatus,
            LocalDateTime originalCreatedAt,
            LocalDateTime archivedAt) {

        IntegrationHistory history =
                new IntegrationHistory();

        history.setMessageId(messageId);
        history.setConnectorId(connectorId);
        history.setCorrelationId(correlationId);
        history.setRequestId(requestId);
        history.setMessageType(messageType);
        history.setSourceSystem(sourceSystem);
        history.setTargetSystem(targetSystem);
        history.setPayloadJson(payloadJson);
        history.setProcessingTimeMs(processingTimeMs);
        history.setMessageStatus(messageStatus);
        history.setOriginalCreatedAt(originalCreatedAt);
        history.setArchivedAt(archivedAt);

        return history;
    }
}