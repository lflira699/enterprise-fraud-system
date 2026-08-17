package com.efs.modules.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerHistoryResponse {

    private UUID customerHistoryId;
    private UUID customerId;
    private String eventType;
    private String eventDescription;
    private String previousStatus;
    private String newStatus;
    private String previousRiskLevel;
    private String newRiskLevel;
    private BigDecimal previousRiskScore;
    private BigDecimal newRiskScore;
    private LocalDateTime eventTimestamp;
    private String sourceReference;
    private LocalDateTime createdAt;
    private UUID createdBy;

    public UUID getCustomerHistoryId() {
        return customerHistoryId;
    }

    public void setCustomerHistoryId(UUID customerHistoryId) {
        this.customerHistoryId = customerHistoryId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
}