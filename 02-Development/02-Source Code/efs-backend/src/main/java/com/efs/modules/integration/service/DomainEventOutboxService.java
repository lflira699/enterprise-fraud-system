package com.efs.modules.integration.service;

import com.efs.modules.integration.entity.OutboxEvent;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DomainEventOutboxService {

    private static final String STATUS_PENDING =
            "PENDING";

    private final OutboxEventRepository outboxEventRepository;

    public DomainEventOutboxService(
            OutboxEventRepository outboxEventRepository) {

        this.outboxEventRepository =
                outboxEventRepository;
    }

    @Transactional
    public UUID persist(
            String aggregateType,
            UUID aggregateId,
            DomainEventEnvelope envelope) {

        OutboxEvent outboxEvent =
                new OutboxEvent();

        outboxEvent.setAggregateType(
                aggregateType
        );

        outboxEvent.setAggregateId(
                aggregateId
        );

        outboxEvent.setEventType(
                envelope.getEventType()
        );

        outboxEvent.setPayload(
                new LinkedHashMap<>()
        );

        outboxEvent.setOccurredAt(
                envelope.getOccurredAt()
        );

        outboxEvent.setCorrelationId(
                envelope.getCorrelationId()
        );

        outboxEvent.setStatus(
                STATUS_PENDING
        );

        outboxEvent.setAttemptCount(
                0
        );

        OutboxEvent savedEvent =
                outboxEventRepository.saveAndFlush(
                        outboxEvent
                );

        envelope.setMessageId(
                savedEvent.getId()
        );

        savedEvent.setPayload(
                buildEnvelopePayload(envelope)
        );

        outboxEventRepository.save(
                savedEvent
        );

        return savedEvent.getId();
    }

    private Map<String, Object> buildEnvelopePayload(
            DomainEventEnvelope envelope) {

        Map<String, Object> event =
                new LinkedHashMap<>();

        event.put(
                "messageId",
                toString(envelope.getMessageId())
        );

        event.put(
                "eventType",
                envelope.getEventType()
        );

        event.put(
                "schemaVersion",
                envelope.getSchemaVersion()
        );

        event.put(
                "occurredAt",
                envelope.getOccurredAt() == null
                        ? null
                        : envelope.getOccurredAt().toString()
        );

        event.put(
                "producer",
                envelope.getProducer()
        );

        event.put(
                "correlationId",
                toString(envelope.getCorrelationId())
        );

        event.put(
                "causationId",
                toString(envelope.getCausationId())
        );

        event.put(
                "tenantId",
                toString(envelope.getTenantId())
        );

        event.put(
                "payload",
                envelope.getPayload()
        );

        event.put(
                "metadata",
                envelope.getMetadata()
        );

        return event;
    }

    private String toString(UUID value) {

        return value == null
                ? null
                : value.toString();
    }
}