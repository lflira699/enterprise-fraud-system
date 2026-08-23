package com.efs.modules.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CaseHistoryRequest {

    @NotBlank
    @Size(max = 60)
    private String eventType;

    @NotBlank
    private String eventDescription;

    private String previousValue;

    private String newValue;

    @NotNull
    private UUID changedBy;

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
}