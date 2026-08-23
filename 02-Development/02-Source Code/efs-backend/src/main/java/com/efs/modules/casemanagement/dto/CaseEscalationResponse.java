package com.efs.modules.casemanagement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CaseEscalationResponse {

    private UUID escalationId;
    private UUID caseId;
    private String escalationLevel;
    private String fromTeam;
    private String toTeam;
    private String escalationReason;
    private UUID escalatedBy;
    private LocalDateTime escalatedAt;
    private LocalDateTime resolvedAt;

    public UUID getEscalationId() {
        return escalationId;
    }

    public void setEscalationId(
            UUID escalationId) {

        this.escalationId =
                escalationId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(
            UUID caseId) {

        this.caseId =
                caseId;
    }

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

    public LocalDateTime getEscalatedAt() {
        return escalatedAt;
    }

    public void setEscalatedAt(
            LocalDateTime escalatedAt) {

        this.escalatedAt =
                escalatedAt;
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