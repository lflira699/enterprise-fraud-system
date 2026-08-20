package com.efs.modules.rules.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class RuleHistoryResponse {

    private UUID historyId;
    private String entityType;
    private UUID entityId;
    private String operationType;
    private Map<String, Object> previousValue;
    private Map<String, Object> currentValue;
    private String changeReason;
    private UUID changedBy;
    private LocalDateTime changedAt;
    private UUID correlationId;

    public UUID getHistoryId() {
        return historyId;
    }

    public void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Map<String, Object> getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(Map<String, Object> previousValue) {
        this.previousValue = previousValue;
    }

    public Map<String, Object> getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Map<String, Object> currentValue) {
        this.currentValue = currentValue;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(UUID changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }
}