package com.efs.modules.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public class AuditConfigurationChangeRequest {

    @NotNull
    private UUID auditEventId;

    @NotBlank
    @Size(max = 150)
    private String configurationKey;

    private Map<String, Object> previousValue;

    private Map<String, Object> currentValue;

    @NotNull
    private UUID changedBy;

    private String changeReason;

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
}