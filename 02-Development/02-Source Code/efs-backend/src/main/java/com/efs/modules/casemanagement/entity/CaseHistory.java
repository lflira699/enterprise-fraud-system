package com.efs.modules.casemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "case_history",
        schema = "case_management"
)
public class CaseHistory {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    @Column(
            name = "history_id",
            nullable = false
    )
    private UUID historyId;

    @Column(
            name = "case_id",
            nullable = false
    )
    private UUID caseId;

    @Column(
            name = "event_type",
            nullable = false,
            length = 60
    )
    private String eventType;

    @Column(
            name = "event_description",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String eventDescription;

    @Column(
            name = "previous_value",
            columnDefinition = "TEXT"
    )
    private String previousValue;

    @Column(
            name = "new_value",
            columnDefinition = "TEXT"
    )
    private String newValue;

    @Column(
            name = "changed_by",
            nullable = false
    )
    private UUID changedBy;

    @Column(
            name = "changed_at",
            nullable = false
    )
    private LocalDateTime changedAt;

    public CaseHistory() {
    }

    public UUID getHistoryId() {
        return historyId;
    }

    public void setHistoryId(
            UUID historyId) {

        this.historyId =
                historyId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(
            UUID caseId) {

        this.caseId =
                caseId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(
            String eventType) {

        this.eventType =
                eventType;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(
            String eventDescription) {

        this.eventDescription =
                eventDescription;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(
            String previousValue) {

        this.previousValue =
                previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(
            String newValue) {

        this.newValue =
                newValue;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(
            UUID changedBy) {

        this.changedBy =
                changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(
            LocalDateTime changedAt) {

        this.changedAt =
                changedAt;
    }
}