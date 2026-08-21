package com.efs.modules.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AlertClosureRequest {

    @NotBlank
    private String investigationResult;

    @NotBlank
    private String closureReason;

    @NotNull
    private UUID closedBy;

    public String getInvestigationResult() {
        return investigationResult;
    }

    public void setInvestigationResult(String investigationResult) {
        this.investigationResult = investigationResult;
    }

    public String getClosureReason() {
        return closureReason;
    }

    public void setClosureReason(String closureReason) {
        this.closureReason = closureReason;
    }

    public UUID getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(UUID closedBy) {
        this.closedBy = closedBy;
    }
}