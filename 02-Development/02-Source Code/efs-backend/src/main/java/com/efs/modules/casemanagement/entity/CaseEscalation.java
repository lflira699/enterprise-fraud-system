package com.efs.modules.casemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "case_escalation",
        schema = "case_management"
)
public class CaseEscalation {

    @Id
    @GeneratedValue
    @Column(
            name = "escalation_id",
            nullable = false
    )
    private UUID escalationId;

    @Column(
            name = "case_id",
            nullable = false
    )
    private UUID caseId;

    @Column(
            name = "escalation_level",
            nullable = false,
            length = 40
    )
    private String escalationLevel;

    @Column(
            name = "from_team",
            length = 100
    )
    private String fromTeam;

    @Column(
            name = "to_team",
            nullable = false,
            length = 100
    )
    private String toTeam;

    @Column(
            name = "escalation_reason",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String escalationReason;

    @Column(
            name = "escalated_by",
            nullable = false
    )
    private UUID escalatedBy;

    @Column(
            name = "escalated_at",
            nullable = false
    )
    private LocalDateTime escalatedAt;

    @Column(
            name = "resolved_at"
    )
    private LocalDateTime resolvedAt;

    public CaseEscalation() {
    }

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