package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationConnector;
import com.efs.modules.integration.entity.IntegrationEndpoint;
import com.efs.modules.integration.entity.IntegrationMessage;
import com.efs.modules.integration.entity.IntegrationRetry;
import org.junit.jupiter.api.BeforeEach;
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
class IntegrationRetryRepositoryIntegrationTest {

    @Autowired
    private IntegrationRetryRepository retryRepository;

    @Autowired
    private IntegrationMessageRepository messageRepository;

    @Autowired
    private IntegrationConnectorRepository connectorRepository;

    @Autowired
    private IntegrationEndpointRepository endpointRepository;

    private UUID messageId;

    @BeforeEach
    void setUp() {

        IntegrationEndpoint endpoint =
                new IntegrationEndpoint();

        endpoint.setEndpointCode(
                "EFS-V90-ENDPOINT-" + UUID.randomUUID()
        );

        endpoint.setEndpointName(
                "V90 Retry Test Endpoint"
        );

        endpoint.setEndpointUrl(
                "https://integration.example.test/v90"
        );

        endpoint.setProtocol(
                "HTTPS"
        );

        endpoint.setAuthenticationType(
                "API_KEY"
        );

        endpoint.setTimeoutSeconds(
                30
        );

        endpoint.setStatus(
                "ACTIVE"
        );

        IntegrationEndpoint savedEndpoint =
                endpointRepository.saveAndFlush(
                        endpoint
                );

        IntegrationConnector connector =
                new IntegrationConnector();

        connector.setEndpointId(
                savedEndpoint.getEndpointId()
        );

        connector.setConnectorName(
                "V90 Retry Test Connector"
        );

        connector.setConnectorType(
                "REST"
        );

        connector.setProvider(
                "EFS"
        );

        connector.setVersion(
                "1.0"
        );

        connector.setStatus(
                "ACTIVE"
        );

        connector.setCreatedAt(
                LocalDateTime.now()
        );

        IntegrationConnector savedConnector =
                connectorRepository.saveAndFlush(
                        connector
                );

        IntegrationMessage message =
                new IntegrationMessage();

        message.setConnectorId(
                savedConnector.getConnectorId()
        );

        message.setCorrelationId(
                UUID.randomUUID()
        );

        message.setRequestId(
                UUID.randomUUID()
        );

        message.setMessageType(
                "REQUEST"
        );

        message.setSourceSystem(
                "EFS"
        );

        message.setTargetSystem(
                "BANK_CORE"
        );

        message.setPayloadJson(
                Map.of(
                        "test",
                        "V90"
                )
        );

        message.setProcessingTimeMs(
                100
        );

        message.setMessageStatus(
                "FAILED"
        );

        message.setCreatedAt(
                LocalDateTime.now()
        );

        IntegrationMessage savedMessage =
                messageRepository.saveAndFlush(
                        message
                );

        messageId =
                savedMessage.getMessageId();

        assertNotNull(messageId);
    }

    @Test
    void shouldSaveIntegrationRetry() {

        LocalDateTime createdAt =
                LocalDateTime.now();

        LocalDateTime nextRetry =
                createdAt.plusMinutes(5);

        IntegrationRetry retry =
                createRetry(
                        messageId,
                        1,
                        "Connection timeout",
                        nextRetry,
                        "PENDING",
                        createdAt,
                        "TIMEOUT"
                );

        IntegrationRetry saved =
                retryRepository.saveAndFlush(
                        retry
                );

        assertNotNull(
                saved.getRetryId()
        );

        assertEquals(
                messageId,
                saved.getMessageId()
        );

        assertEquals(
                1,
                saved.getRetryNumber()
        );

        assertEquals(
                "Connection timeout",
                saved.getErrorDescription()
        );

        assertEquals(
                nextRetry,
                saved.getNextRetry()
        );

        assertEquals(
                "PENDING",
                saved.getRetryStatus()
        );

        assertEquals(
                "TIMEOUT",
                saved.getErrorCode()
        );

        assertNotNull(
                saved.getCreatedAt()
        );
    }

    @Test
    void shouldFindIntegrationRetryById() {

        IntegrationRetry saved =
                retryRepository.saveAndFlush(
                        createRetry(
                                messageId,
                                1,
                                "Temporary integration failure",
                                LocalDateTime.now()
                                        .plusMinutes(5),
                                "PENDING",
                                LocalDateTime.now(),
                                "TEMPORARY_FAILURE"
                        )
                );

        IntegrationRetry result =
                retryRepository
                        .findById(saved.getRetryId())
                        .orElseThrow();

        assertEquals(
                saved.getRetryId(),
                result.getRetryId()
        );

        assertEquals(
                messageId,
                result.getMessageId()
        );

        assertEquals(
                1,
                result.getRetryNumber()
        );

        assertEquals(
                "PENDING",
                result.getRetryStatus()
        );
    }

    @Test
    void shouldFindRetriesByMessageIdOrderedByRetryNumberAsc() {

        LocalDateTime baseTime =
                LocalDateTime.now();

        retryRepository.save(
                createRetry(
                        messageId,
                        3,
                        "Third attempt",
                        baseTime.plusMinutes(15),
                        "PENDING",
                        baseTime.plusMinutes(2),
                        "RETRY_003"
                )
        );

        retryRepository.save(
                createRetry(
                        messageId,
                        1,
                        "First attempt",
                        baseTime.plusMinutes(5),
                        "FAILED",
                        baseTime,
                        "RETRY_001"
                )
        );

        retryRepository.save(
                createRetry(
                        messageId,
                        2,
                        "Second attempt",
                        baseTime.plusMinutes(10),
                        "FAILED",
                        baseTime.plusMinutes(1),
                        "RETRY_002"
                )
        );

        retryRepository.flush();

        List<IntegrationRetry> result =
                retryRepository
                        .findByMessageIdOrderByRetryNumberAsc(
                                messageId
                        );

        assertEquals(
                3,
                result.size()
        );

        assertEquals(
                1,
                result.get(0).getRetryNumber()
        );

        assertEquals(
                2,
                result.get(1).getRetryNumber()
        );

        assertEquals(
                3,
                result.get(2).getRetryNumber()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                retry ->
                                        messageId.equals(
                                                retry.getMessageId()
                                        )
                        )
        );
    }

    @Test
    void shouldFindRetriesByStatusOrderedByCreatedAtDesc() {

        LocalDateTime baseTime =
                LocalDateTime.now();

        retryRepository.save(
                createRetry(
                        messageId,
                        1,
                        "First failed attempt",
                        baseTime.plusMinutes(5),
                        "FAILED",
                        baseTime.minusMinutes(2),
                        "FAILURE_001"
                )
        );

        retryRepository.save(
                createRetry(
                        messageId,
                        2,
                        "Second failed attempt",
                        baseTime.plusMinutes(10),
                        "FAILED",
                        baseTime,
                        "FAILURE_002"
                )
        );

        retryRepository.save(
                createRetry(
                        messageId,
                        3,
                        null,
                        null,
                        "COMPLETED",
                        baseTime.plusMinutes(1),
                        null
                )
        );

        retryRepository.flush();

        List<IntegrationRetry> result =
                retryRepository
                        .findByRetryStatusOrderByCreatedAtDesc(
                                "FAILED"
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                2,
                result.get(0).getRetryNumber()
        );

        assertEquals(
                1,
                result.get(1).getRetryNumber()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                retry ->
                                        "FAILED".equals(
                                                retry.getRetryStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldPersistNullableRetryFields() {

        IntegrationRetry retry =
                createRetry(
                        messageId,
                        1,
                        null,
                        null,
                        "COMPLETED",
                        LocalDateTime.now(),
                        null
                );

        IntegrationRetry saved =
                retryRepository.saveAndFlush(
                        retry
                );

        assertNotNull(
                saved.getRetryId()
        );

        assertEquals(
                messageId,
                saved.getMessageId()
        );

        assertEquals(
                "COMPLETED",
                saved.getRetryStatus()
        );

        assertEquals(
                null,
                saved.getErrorDescription()
        );

        assertEquals(
                null,
                saved.getNextRetry()
        );

        assertEquals(
                null,
                saved.getErrorCode()
        );
    }

    @Test
    void shouldReturnEmptyListWhenMessageIdDoesNotExist() {

        List<IntegrationRetry> result =
                retryRepository
                        .findByMessageIdOrderByRetryNumberAsc(
                                UUID.fromString(
                                        "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                                )
                        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenRetryStatusDoesNotExist() {

        retryRepository.saveAndFlush(
                createRetry(
                        messageId,
                        1,
                        "Known retry",
                        LocalDateTime.now()
                                .plusMinutes(5),
                        "PENDING",
                        LocalDateTime.now(),
                        "KNOWN_ERROR"
                )
        );

        List<IntegrationRetry> result =
                retryRepository
                        .findByRetryStatusOrderByCreatedAtDesc(
                                "UNKNOWN"
                        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private IntegrationRetry createRetry(
            UUID messageId,
            Integer retryNumber,
            String errorDescription,
            LocalDateTime nextRetry,
            String retryStatus,
            LocalDateTime createdAt,
            String errorCode) {

        IntegrationRetry retry =
                new IntegrationRetry();

        retry.setMessageId(
                messageId
        );

        retry.setRetryNumber(
                retryNumber
        );

        retry.setErrorDescription(
                errorDescription
        );

        retry.setNextRetry(
                nextRetry
        );

        retry.setRetryStatus(
                retryStatus
        );

        retry.setCreatedAt(
                createdAt
        );

        retry.setErrorCode(
                errorCode
        );

        return retry;
    }
}