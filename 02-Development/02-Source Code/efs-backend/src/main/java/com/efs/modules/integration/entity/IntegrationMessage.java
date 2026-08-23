package com.efs.modules.integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "integration_message",
        schema = "integration"
)
public class IntegrationMessage {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Column(name = "connector_id", nullable = false)
    private UUID connectorId;

    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(
            name = "message_type",
            nullable = false,
            length = 50
    )
    private String messageType;

    @Column(
            name = "source_system",
            nullable = false,
            length = 100
    )
    private String sourceSystem;

    @Column(
            name = "target_system",
            nullable = false,
            length = 100
    )
    private String targetSystem;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "payload_json",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, Object> payloadJson;

    @Column(name = "processing_time_ms")
    private Integer processingTimeMs;

    @Column(
            name = "message_status",
            nullable = false,
            length = 20
    )
    private String messageStatus;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public IntegrationMessage() {
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public UUID getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(UUID connectorId) {
        this.connectorId = connectorId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(String targetSystem) {
        this.targetSystem = targetSystem;
    }

    public Map<String, Object> getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(Map<String, Object> payloadJson) {
        this.payloadJson = payloadJson;
    }

    public Integer getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Integer processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}