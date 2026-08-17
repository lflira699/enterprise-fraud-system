package com.efs.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

public class CustomerRiskProfileRequest {

    private BigDecimal currentRiskScore;

    @NotBlank
    private String riskLevel;

    private BigDecimal behaviorScore;
    private BigDecimal fraudScore;
    private BigDecimal amlScore;
    private BigDecimal kycScore;
    private BigDecimal deviceScore;
    private BigDecimal sanctionsScore;
    private BigDecimal pepScore;
    private BigDecimal watchlistScore;

    private UUID createdBy;
    private UUID updatedBy;

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

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }
}