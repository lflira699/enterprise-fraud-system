package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationConnector;
import com.efs.modules.integration.entity.IntegrationEndpoint;
import com.efs.modules.integration.entity.IntegrationMessage;
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
class IntegrationMessageRepositoryIntegrationTest {

    @Autowired
    private IntegrationMessageRepository messageRepository;

    @Autowired
    private IntegrationConnectorRepository connectorRepository;

    @Autowired
    private IntegrationEndpointRepository endpointRepository;

    private UUID connectorId;

    @BeforeEach
    void setUp() {

        IntegrationEndpoint endpoint =
                new IntegrationEndpoint();

        endpoint.setEndpointCode(
                "EFS-V85-ENDPOINT"
        );

        endpoint.setEndpointName(
                "V85 Message Test Endpoint"
        );

        endpoint.setEndpointUrl(
                "https://integration.example.test/v85"
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
                "V85 Message Test Connector"
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

        connectorId =
                savedConnector.getConnectorId();

        assertNotNull(connectorId);
    }

    @Test
    void shouldSaveIntegrationMessage() {

        UUID correlationId =
                UUID.randomUUID();

        UUID requestId =
                UUID.randomUUID();

        IntegrationMessage message =
                createMessage(
                        connectorId,
                        correlationId,
                        requestId,
                        "REQUEST",
                        "EFS",
                        "BANK_CORE",
                        "PENDING",
                        LocalDateTime.now()
                );

        IntegrationMessage saved =
                messageRepository.saveAndFlush(
                        message
                );

        assertNotNull(
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
                "PENDING",
                saved.getMessageStatus()
        );

        assertNotNull(
                saved.getPayloadJson()
        );

        assertNotNull(
                saved.getCreatedAt()
        );
    }

    @Test
    void shouldFindIntegrationMessageById() {

        IntegrationMessage saved =
                messageRepository.saveAndFlush(
                        createMessage(
                                connectorId,
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "REQUEST",
                                "EFS",
                                "BANK_CORE",
                                "PROCESSED",
                                LocalDateTime.now()
                        )
                );

        IntegrationMessage result =
                messageRepository
                        .findById(saved.getMessageId())
                        .orElseThrow();

        assertEquals(
                saved.getMessageId(),
                result.getMessageId()
        );

        assertEquals(
                connectorId,
                result.getConnectorId()
        );

        assertEquals(
                "PROCESSED",
                result.getMessageStatus()
        );
    }

    @Test
    void shouldFindMessagesByConnectorIdOrderedByCreatedAtDesc() {

        LocalDateTime baseTime =
                LocalDateTime.now();

        messageRepository.save(
                createMessage(
                        connectorId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "REQUEST",
                        "EFS",
                        "BANK_CORE",
                        "PROCESSED",
                        baseTime.minusMinutes(2)
                )
        );

        messageRepository.save(
                createMessage(
                        connectorId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "RESPONSE",
                        "BANK_CORE",
                        "EFS",
                        "PROCESSED",
                        baseTime
                )
        );

        messageRepository.flush();

        List<IntegrationMessage> result =
                messageRepository
                        .findByConnectorIdOrderByCreatedAtDesc(
                                connectorId
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                "RESPONSE",
                result.get(0).getMessageType()
        );

        assertEquals(
                "REQUEST",
                result.get(1).getMessageType()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                message ->
                                        connectorId.equals(
                                                message.getConnectorId()
                                        )
                        )
        );
    }

    @Test
    void shouldFindMessagesByCorrelationIdOrderedByCreatedAtDesc() {

        UUID correlationId =
                UUID.randomUUID();

        LocalDateTime baseTime =
                LocalDateTime.now();

        messageRepository.save(
                createMessage(
                        connectorId,
                        correlationId,
                        UUID.randomUUID(),
                        "REQUEST",
                        "EFS",
                        "BANK_CORE",
                        "PROCESSED",
                        baseTime.minusMinutes(1)
                )
        );

        messageRepository.save(
                createMessage(
                        connectorId,
                        correlationId,
                        UUID.randomUUID(),
                        "RESPONSE",
                        "BANK_CORE",
                        "EFS",
                        "PROCESSED",
                        baseTime
                )
        );

        messageRepository.save(
                createMessage(
                        connectorId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "REQUEST",
                        "EFS",
                        "OTHER_SYSTEM",
                        "PROCESSED",
                        baseTime.plusMinutes(1)
                )
        );

        messageRepository.flush();

        List<IntegrationMessage> result =
                messageRepository
                        .findByCorrelationIdOrderByCreatedAtDesc(
                                correlationId
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                "RESPONSE",
                result.get(0).getMessageType()
        );

        assertEquals(
                "REQUEST",
                result.get(1).getMessageType()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                message ->
                                        correlationId.equals(
                                                message.getCorrelationId()
                                        )
                        )
        );
    }

    @Test
    void shouldFindMessagesByStatusOrderedByCreatedAtDesc() {

        LocalDateTime baseTime =
                LocalDateTime.now();

        messageRepository.save(
                createMessage(
                        connectorId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "REQUEST",
                        "EFS",
                        "BANK_CORE",
                        "PROCESSED",
                        baseTime.minusMinutes(2)
                )
        );

        messageRepository.save(
                createMessage(
                        connectorId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "RESPONSE",
                        "BANK_CORE",
                        "EFS",
                        "PROCESSED",
                        baseTime
                )
        );

        messageRepository.save(
                createMessage(
                        connectorId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "REQUEST",
                        "EFS",
                        "BANK_CORE",
                        "FAILED",
                        baseTime.plusMinutes(1)
                )
        );

        messageRepository.flush();

        List<IntegrationMessage> result =
                messageRepository
                        .findByMessageStatusOrderByCreatedAtDesc(
                                "PROCESSED"
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                "RESPONSE",
                result.get(0).getMessageType()
        );

        assertEquals(
                "REQUEST",
                result.get(1).getMessageType()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                message ->
                                        "PROCESSED".equals(
                                                message.getMessageStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldPersistJsonPayloadAndProcessingTime() {

        IntegrationMessage message =
                createMessage(
                        connectorId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "REQUEST",
                        "EFS",
                        "BANK_CORE",
                        "PROCESSED",
                        LocalDateTime.now()
                );

        message.setPayloadJson(
                Map.of(
                        "transactionReference",
                        "TX-V85-001",
                        "riskScore",
                        75
                )
        );

        message.setProcessingTimeMs(
                125
        );

        IntegrationMessage saved =
                messageRepository.saveAndFlush(
                        message
                );

        assertEquals(
                "TX-V85-001",
                saved.getPayloadJson()
                        .get("transactionReference")
        );

        assertEquals(
                75,
                ((Number) saved.getPayloadJson()
                        .get("riskScore")).intValue()
        );

        assertEquals(
                125,
                saved.getProcessingTimeMs()
        );
    }

    @Test
    void shouldReturnEmptyListWhenCorrelationIdDoesNotExist() {

        List<IntegrationMessage> result =
                messageRepository
                        .findByCorrelationIdOrderByCreatedAtDesc(
                                UUID.fromString(
                                        "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                                )
                        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenMessageStatusDoesNotExist() {

        messageRepository.saveAndFlush(
                createMessage(
                        connectorId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "REQUEST",
                        "EFS",
                        "BANK_CORE",
                        "PROCESSED",
                        LocalDateTime.now()
                )
        );

        List<IntegrationMessage> result =
                messageRepository
                        .findByMessageStatusOrderByCreatedAtDesc(
                                "UNKNOWN"
                        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private IntegrationMessage createMessage(
            UUID connectorId,
            UUID correlationId,
            UUID requestId,
            String messageType,
            String sourceSystem,
            String targetSystem,
            String messageStatus,
            LocalDateTime createdAt) {

        IntegrationMessage message =
                new IntegrationMessage();

        message.setConnectorId(
                connectorId
        );

        message.setCorrelationId(
                correlationId
        );

        message.setRequestId(
                requestId
        );

        message.setMessageType(
                messageType
        );

        message.setSourceSystem(
                sourceSystem
        );

        message.setTargetSystem(
                targetSystem
        );

        message.setPayloadJson(
                Map.of(
                        "test",
                        "V85"
                )
        );

        message.setProcessingTimeMs(
                100
        );

        message.setMessageStatus(
                messageStatus
        );

        message.setCreatedAt(
                createdAt
        );

        return message;
    }
}