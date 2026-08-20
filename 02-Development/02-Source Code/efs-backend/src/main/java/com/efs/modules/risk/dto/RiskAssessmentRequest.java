package com.efs.modules.risk.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class RiskAssessmentRequest {

    @NotNull
    private UUID transactionId;

    @NotNull
    @Size(max = 40)
    private String assessmentType;

    @NotNull
    @Size(max = 40)
    private String assessmentStage;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal overallRiskScore;

    @NotNull
    @Size(max = 20)
    private String riskLevel;

    @Size(max = 40)
    private String riskCategory;

    @NotNull
    @Size(max = 40)
    private String assessmentResult;

    private BigDecimal rulesScore;

    private BigDecimal machineLearningScore;

    private BigDecimal behavioralScore;

    private BigDecimal customerScore;

    private BigDecimal geographicScore;

    private BigDecimal deviceScore;

    @DecimalMin("0.00")
    private BigDecimal confidenceScore;

    @Size(max = 100)
    private String modelName;

    @Size(max = 40)
    private String modelVersion;

    private Long processingTimeMs;

    private Map<String, Object> assessmentDetails;

    private UUID createdBy;

    private UUID updatedBy;

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