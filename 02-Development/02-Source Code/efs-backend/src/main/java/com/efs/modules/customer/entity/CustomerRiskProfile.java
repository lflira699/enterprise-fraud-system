package com.efs.modules.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_risk_profile", schema = "customer")
public class CustomerRiskProfile {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "profile_id", nullable = false)
    private UUID profileId;

    @Column(name = "customer_id", nullable = false, unique = true)
    private UUID customerId;

    @Column(name = "current_risk_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal currentRiskScore;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "last_calculation")
    private LocalDateTime lastCalculation;

    @Column(name = "behavior_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal behaviorScore;

    @Column(name = "fraud_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal fraudScore;

    @Column(name = "aml_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal amlScore;

    @Column(name = "kyc_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal kycScore;

    @Column(name = "device_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal deviceScore;

    @Column(name = "sanctions_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal sanctionsScore;

    @Column(name = "pep_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal pepScore;

    @Column(name = "watchlist_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal watchlistScore;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Version
    @Column(name = "record_version", nullable = false)
    private Integer recordVersion;

    public CustomerRiskProfile() {
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getCurrentRiskScore() {
        return currentRiskScore;
    }

    public void setCurrentRiskScore(BigDecimal currentRiskScore) {
        this.currentRiskScore = currentRiskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getLastCalculation() {
        return lastCalculation;
    }

    public void setLastCalculation(LocalDateTime lastCalculation) {
        this.lastCalculation = lastCalculation;
    }

    public BigDecimal getBehaviorScore() {
        return behaviorScore;
    }

    public void setBehaviorScore(BigDecimal behaviorScore) {
        this.behaviorScore = behaviorScore;
    }

    public BigDecimal getFraudScore() {
        return fraudScore;
    }

    public void setFraudScore(BigDecimal fraudScore) {
        this.fraudScore = fraudScore;
    }

    public BigDecimal getAmlScore() {
        return amlScore;
    }

    public void setAmlScore(BigDecimal amlScore) {
        this.amlScore = amlScore;
    }

    public BigDecimal getKycScore() {
        return kycScore;
    }

    public void setKycScore(BigDecimal kycScore) {
        this.kycScore = kycScore;
    }

    public BigDecimal getDeviceScore() {
        return deviceScore;
    }

    public void setDeviceScore(BigDecimal deviceScore) {
        this.deviceScore = deviceScore;
    }

    public BigDecimal getSanctionsScore() {
        return sanctionsScore;
    }

    public void setSanctionsScore(BigDecimal sanctionsScore) {
        this.sanctionsScore = sanctionsScore;
    }

    public BigDecimal getPepScore() {
        return pepScore;
    }

    public void setPepScore(BigDecimal pepScore) {
        this.pepScore = pepScore;
    }

    public BigDecimal getWatchlistScore() {
        return watchlistScore;
    }

    public void setWatchlistScore(BigDecimal watchlistScore) {
        this.watchlistScore = watchlistScore;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Integer recordVersion) {
        this.recordVersion = recordVersion;
    }
}