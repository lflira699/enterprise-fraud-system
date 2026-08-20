package com.efs.modules.risk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class RiskAssessmentResponse {

    private UUID riskAssessmentId;
    private UUID transactionId;
    private String assessmentType;
    private String assessmentStage;
    private BigDecimal overallRiskScore;
    private String riskLevel;
    private String riskCategory;
    private String assessmentResult;
    private BigDecimal rulesScore;
    private BigDecimal machineLearningScore;
    private BigDecimal behavioralScore;
    private BigDecimal customerScore;
    private BigDecimal geographicScore;
    private BigDecimal deviceScore;
    private BigDecimal confidenceScore;
    private String modelName;
    private String modelVersion;
    private LocalDateTime assessmentTimestamp;
    private Long processingTimeMs;
    private Map<String, Object> assessmentDetails;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
    private LocalDateTime deletedAt;
    private UUID deletedBy;
    private Integer recordVersion;

    public UUID getRiskAssessmentId() {
        return riskAssessmentId;
    }

    public void setRiskAssessmentId(UUID riskAssessmentId) {
        this.riskAssessmentId = riskAssessmentId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public String getAssessmentStage() {
        return assessmentStage;
    }

    public void setAssessmentStage(String assessmentStage) {
        this.assessmentStage = assessmentStage;
    }

    public BigDecimal getOverallRiskScore() {
        return overallRiskScore;
    }

    public void setOverallRiskScore(BigDecimal overallRiskScore) {
        this.overallRiskScore = overallRiskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(String riskCategory) {
        this.riskCategory = riskCategory;
    }

    public String getAssessmentResult() {
        return assessmentResult;
    }

    public void setAssessmentResult(String assessmentResult) {
        this.assessmentResult = assessmentResult;
    }

    public BigDecimal getRulesScore() {
        return rulesScore;
    }

    public void setRulesScore(BigDecimal rulesScore) {
        this.rulesScore = rulesScore;
    }

    public BigDecimal getMachineLearningScore() {
        return machineLearningScore;
    }

    public void setMachineLearningScore(BigDecimal machineLearningScore) {
        this.machineLearningScore = machineLearningScore;
    }

    public BigDecimal getBehavioralScore() {
        return behavioralScore;
    }

    public void setBehavioralScore(BigDecimal behavioralScore) {
        this.behavioralScore = behavioralScore;
    }

    public BigDecimal getCustomerScore() {
        return customerScore;
    }

    public void setCustomerScore(BigDecimal customerScore) {
        this.customerScore = customerScore;
    }

    public BigDecimal getGeographicScore() {
        return geographicScore;
    }

    public void setGeographicScore(BigDecimal geographicScore) {
        this.geographicScore = geographicScore;
    }

    public BigDecimal getDeviceScore() {
        return deviceScore;
    }

    public void setDeviceScore(BigDecimal deviceScore) {
        this.deviceScore = deviceScore;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public LocalDateTime getAssessmentTimestamp() {
        return assessmentTimestamp;
    }

    public void setAssessmentTimestamp(LocalDateTime assessmentTimestamp) {
        this.assessmentTimestamp = assessmentTimestamp;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public Map<String, Object> getAssessmentDetails() {
        return assessmentDetails;
    }

    public void setAssessmentDetails(Map<String, Object> assessmentDetails) {
        this.assessmentDetails = assessmentDetails;
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

    public UUID getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Integer getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Integer recordVersion) {
        this.recordVersion = recordVersion;
    }
}