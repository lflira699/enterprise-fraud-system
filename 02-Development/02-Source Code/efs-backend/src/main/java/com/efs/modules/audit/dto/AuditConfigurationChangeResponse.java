package com.efs.modules.audit.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class AuditConfigurationChangeResponse {

    private UUID configurationChangeId;
    private UUID auditEventId;
    private String configurationKey;
    private Map<String, Object> previousValue;
    private Map<String, Object> currentValue;
    private UUID changedBy;
    private String changeReason;
    private LocalDateTime changedAt;

    public UUID getConfigurationChangeId() {
        return configurationChangeId;
    }

    public void setConfigurationChangeId(UUID configurationChangeId) {
        this.configurationChangeId = configurationChangeId;
    }

    public UUID getAuditEventId() {
        return auditEventId;
    }

    public void setAuditEventId(UUID auditEventId) {
        this.auditEventId = auditEventId;
    }

    public String getConfigurationKey() {
        return configurationKey;
    }

    public void setConfigurationKey(String configurationKey) {
        this.configurationKey = configurationKey;
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

    public UUID getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(UUID changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}