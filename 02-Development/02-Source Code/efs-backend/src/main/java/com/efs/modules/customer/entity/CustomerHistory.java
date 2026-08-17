package com.efs.modules.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_history", schema = "customer")
public class CustomerHistory {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "customer_history_id", nullable = false)
    private UUID customerHistoryId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "event_description", length = 500)
    private String eventDescription;

    @Column(name = "previous_status", length = 30)
    private String previousStatus;

    @Column(name = "new_status", length = 30)
    private String newStatus;

    @Column(name = "previous_risk_level", length = 20)
    private String previousRiskLevel;

    @Column(name = "new_risk_level", length = 20)
    private String newRiskLevel;

    @Column(name = "previous_risk_score", precision = 8, scale = 2)
    private BigDecimal previousRiskScore;

    @Column(name = "new_risk_score", precision = 8, scale = 2)
    private BigDecimal newRiskScore;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "source_reference", length = 150)
    private String sourceReference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    public CustomerHistory() {
    }

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