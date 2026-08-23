package com.efs.modules.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class CaseEscalationRequest {

    @NotBlank
    @Size(max = 40)
    private String escalationLevel;

    @Size(max = 100)
    private String fromTeam;

    @NotBlank
    @Size(max = 100)
    private String toTeam;

    @NotBlank
    private String escalationReason;

    @NotNull
    private UUID escalatedBy;

    private LocalDateTime resolvedAt;

    public String getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(
            String escalationLevel) {

        this.escalationLevel =
                escalationLevel;
    }

    public String getFromTeam() {
        return fromTeam;
    }

    public void setFromTeam(
            String fromTeam) {

        this.fromTeam =
                fromTeam;
    }

    public String getToTeam() {
        return toTeam;
    }

    public void setToTeam(
            String toTeam) {

        this.toTeam =
                toTeam;
    }

    public String getEscalationReason() {
        return escalationReason;
    }

    public void setEscalationReason(
            String escalationReason) {

        this.escalationReason =
                escalationReason;
    }

    public UUID getEscalatedBy() {
        return escalatedBy;
    }

    public void setEscalatedBy(
            UUID escalatedBy) {

        this.escalatedBy =
                escalatedBy;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(
            LocalDateTime resolvedAt) {

        this.resolvedAt =
                resolvedAt;
    }
}