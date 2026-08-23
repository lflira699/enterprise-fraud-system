package com.efs.modules.casemanagement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CaseHistoryResponse {

    private UUID historyId;
    private UUID caseId;
    private String eventType;
    private String eventDescription;
    private String previousValue;
    private String newValue;
    private UUID changedBy;
    private LocalDateTime changedAt;

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