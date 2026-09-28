package com.efs.modules.event.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "fraud_event",
        schema = "event_management"
)
public class FraudEvent {

    @Id
    @Generated(event = EventType.INSERT)
    @Column(
            name = "fraud_event_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private UUID fraudEventId;

    @Column(
            name = "organization_id",
            nullable = false,
            updatable = false
    )
    private UUID organizationId;

    @Column(
            name = "tenant_id",
            nullable = false,
            updatable = false
    )
    private UUID tenantId;

    @Column(
            name = "transaction_id",
            updatable = false
    )
    private UUID transactionId;

    @Column(
            name = "event_type",
            nullable = false,
            length = 100,
            updatable = false
    )
    private String eventType;

    @Column(
            name = "source_type",
            nullable = false,
            length = 60,
            updatable = false
    )
    private String sourceType;

    @Column(
            name = "source_reference",
            length = 255,
            updatable = false
    )
    private String sourceReference;

    @Column(
            name = "idempotency_key",
            nullable = false,
            length = 255,
            updatable = false
    )
    private String idempotencyKey;

    @Column(
            name = "correlation_id",
            nullable = false,
            updatable = false
    )
    private UUID correlationId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "normalized_payload",
            nullable = false,
            columnDefinition = "jsonb",
            updatable = false
    )
    private JsonNode normalizedPayload;

    @Column(
            name = "occurred_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime occurredAt;

    @Column(
            name = "received_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime receivedAt;

    @Generated(event = EventType.INSERT)
    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    protected FraudEvent() {
    }

    public FraudEvent(
            UUID organizationId,
            UUID tenantId,
            UUID transactionId,
            String eventType,
            String sourceType,
            String sourceReference,
            String idempotencyKey,
            UUID correlationId,
            JsonNode normalizedPayload,
            LocalDateTime occurredAt,
            LocalDateTime receivedAt) {

        this.organizationId = organizationId;
        this.tenantId = tenantId;
        this.transactionId = transactionId;
        this.eventType = eventType;
        this.sourceType = sourceType;
        this.sourceReference = sourceReference;
        this.idempotencyKey = idempotencyKey;
        this.correlationId = correlationId;
        this.normalizedPayload = normalizedPayload;
        this.occurredAt = occurredAt;
        this.receivedAt = receivedAt;
    }

    public UUID getFraudEventId() {
        return fraudEventId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getSourceReference() {
        return sourceReference;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public JsonNode getNormalizedPayload() {
        return normalizedPayload;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
