package com.efs.modules.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerRiskProfileResponse {

    private UUID profileId;
    private UUID customerId;
    private BigDecimal currentRiskScore;
    private String riskLevel;
    private LocalDateTime lastCalculation;
    private BigDecimal behaviorScore;
    private BigDecimal fraudScore;
    private BigDecimal amlScore;
    private BigDecimal kycScore;
    private BigDecimal deviceScore;
    private BigDecimal sanctionsScore;
    private BigDecimal pepScore;
    private BigDecimal watchlistScore;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
    private LocalDateTime deletedAt;
    private Integer recordVersion;

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