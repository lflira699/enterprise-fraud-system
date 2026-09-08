package com.efs.modules.rules.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class RuleVersionPublishRequest {

    @NotNull
    private UUID approvedBy;

    private String changeReason;

    private UUID correlationId;

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
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