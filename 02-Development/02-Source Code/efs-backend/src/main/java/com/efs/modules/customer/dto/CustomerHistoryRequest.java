package com.efs.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerHistoryRequest {

    @NotBlank
    @Size(max = 50)
    private String eventType;

    @Size(max = 500)
    private String eventDescription;

    @Size(max = 30)
    private String previousStatus;

    @Size(max = 30)
    private String newStatus;

    @Size(max = 20)
    private String previousRiskLevel;

    @Size(max = 20)
    private String newRiskLevel;

    private BigDecimal previousRiskScore;

    private BigDecimal newRiskScore;

    private LocalDateTime eventTimestamp;

    @Size(max = 150)
    private String sourceReference;

    private UUID createdBy;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getPreviousRiskLevel() {
        return previousRiskLevel;
    }

    public void setPreviousRiskLevel(String previousRiskLevel) {
        this.previousRiskLevel = previousRiskLevel;
    }

    public String getNewRiskLevel() {
        return newRiskLevel;
    }

    public void setNewRiskLevel(String newRiskLevel) {
        this.newRiskLevel = newRiskLevel;
    }

    public BigDecimal getPreviousRiskScore() {
        return previousRiskScore;
    }

    public void setPreviousRiskScore(BigDecimal previousRiskScore) {
        this.previousRiskScore = previousRiskScore;
    }

    public BigDecimal getNewRiskScore() {
        return newRiskScore;
    }

    public void setNewRiskScore(BigDecimal newRiskScore) {
        this.newRiskScore = newRiskScore;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getSourceReference() {
        return sourceReference;
    }

    public void setSourceReference(String sourceReference) {
        this.sourceReference = sourceReference;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
}