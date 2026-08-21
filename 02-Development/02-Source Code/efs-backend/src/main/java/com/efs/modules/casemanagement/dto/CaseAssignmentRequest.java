package com.efs.modules.casemanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CaseAssignmentRequest {

    private UUID assignedFrom;

    @NotNull
    private UUID assignedTo;

    @Size(max = 100)
    private String assignedTeam;

    private String assignmentReason;

    public UUID getAssignedFrom() {
        return assignedFrom;
    }

    public void setAssignedFrom(UUID assignedFrom) {
        this.assignedFrom = assignedFrom;
    }

    public UUID getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UUID assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(String assignedTeam) {
        this.assignedTeam = assignedTeam;
    }

    public String getAssignmentReason() {
        return assignmentReason;
    }

    public void setAssignmentReason(String assignmentReason) {
        this.assignmentReason = assignmentReason;
    }
}