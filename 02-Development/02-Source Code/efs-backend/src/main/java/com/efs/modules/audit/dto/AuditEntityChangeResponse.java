package com.efs.modules.audit.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class AuditEntityChangeResponse {

    private UUID changeId;
    private UUID auditEventId;
    private String entityType;
    private UUID entityId;
    private String operation;
    private Map<String, Object> previousValue;
    private Map<String, Object> currentValue;
    private LocalDateTime changedAt;

    public UUID getChangeId() {
        return changeId;
    }

    public void setChangeId(UUID changeId) {
        this.changeId = changeId;
    }

    public UUID getAuditEventId() {
        return auditEventId;
    }

    public void setAuditEventId(UUID auditEventId) {
        this.auditEventId = auditEventId;
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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}