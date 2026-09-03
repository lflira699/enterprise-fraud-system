package com.efs.modules.integration.service;

import com.efs.modules.integration.entity.OutboxEvent;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.repository.OutboxEventRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class DomainEventOutboxServiceIntegrationTest {

    @Autowired
    private DomainEventOutboxService domainEventOutboxService;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistApprovedDomainEventEnvelopeContract() {

        UUID aggregateId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        UUID causationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        LocalDateTime occurredAt =
                LocalDateTime.now();

        DomainEventEnvelope envelope =
                new DomainEventEnvelope();

        envelope.setEventType(
                "DOMAIN_EVENT_CONTRACT_TEST"
        );

        envelope.setSchemaVersion(
                "1.0"
        );

        envelope.setOccurredAt(
                occurredAt
        );

        envelope.setProducer(
                "INTEGRATION_TEST"
        );

        envelope.setCorrelationId(
                correlationId
        );

        envelope.setCausationId(
                causationId
        );

        envelope.setTenantId(
                tenantId
        );

        envelope.setPayload(
                Map.of(
                        "aggregateId",
                        aggregateId.toString(),
                        "result",
                        "TEST"
                )
        );

        envelope.setMetadata(
                Map.of(
                        "source",
                        "domain-event-outbox-service-test"
                )
        );

        UUID messageId =
                domainEventOutboxService.persist(
                        "INTEGRATION_TEST",
                        aggregateId,
                        envelope
                );

        entityManager.flush();
        entityManager.clear();

        OutboxEvent persisted =
                outboxEventRepository
                        .findById(messageId)
                        .orElseThrow();

        assertNotNull(messageId);

        assertEquals(
                messageId,
                envelope.getMessageId()
        );

        assertEquals(
                messageId,
                persisted.getId()
        );

        assertEquals(
                "INTEGRATION_TEST",
                persisted.getAggregateType()
        );

        assertEquals(
                aggregateId,
                persisted.getAggregateId()
        );

        assertEquals(
                "DOMAIN_EVENT_CONTRACT_TEST",
                persisted.getEventType()
        );

        assertEquals(
                correlationId,
                persisted.getCorrelationId()
        );

        assertEquals(
                "PENDING",
                persisted.getStatus()
        );

        assertEquals(
                Integer.valueOf(0),
                persisted.getAttemptCount()
        );

        Map<String, Object> storedEnvelope =
                persisted.getPayload();

        assertNotNull(storedEnvelope);

        assertEquals(
                messageId.toString(),
                storedEnvelope.get("messageId")
        );

        assertEquals(
                "DOMAIN_EVENT_CONTRACT_TEST",
                storedEnvelope.get("eventType")
        );

        assertEquals(
                "1.0",
                storedEnvelope.get("schemaVersion")
        );

        assertEquals(
                occurredAt.toString(),
                storedEnvelope.get("occurredAt")
        );

        assertEquals(
                "INTEGRATION_TEST",
                storedEnvelope.get("producer")
        );

        assertEquals(
                correlationId.toString(),
                storedEnvelope.get("correlationId")
        );

        assertEquals(
                causationId.toString(),
                storedEnvelope.get("causationId")
        );

        assertEquals(
                tenantId.toString(),
                storedEnvelope.get("tenantId")
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> storedPayload =
                (Map<String, Object>)
                        storedEnvelope.get("payload");

        assertEquals(
                aggregateId.toString(),
                storedPayload.get("aggregateId")
        );

        assertEquals(
                "TEST",
                storedPayload.get("result")
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> storedMetadata =
                (Map<String, Object>)
                        storedEnvelope.get("metadata");

        assertEquals(
                "domain-event-outbox-service-test",
                storedMetadata.get("source")
        );
    }
}