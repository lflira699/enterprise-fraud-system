package com.efs.modules.rules.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class RuleActivationRequest {

    @NotNull
    private UUID changedBy;

    private String changeReason;

    private UUID correlationId;

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

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }
}