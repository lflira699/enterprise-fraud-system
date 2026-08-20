package com.efs.modules.risk.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "risk_assessment", schema = "transaction")
public class RiskAssessment {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "risk_assessment_id", nullable = false)
    private UUID riskAssessmentId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "assessment_type", nullable = false, length = 40)
    private String assessmentType;

    @Column(name = "assessment_stage", nullable = false, length = 40)
    private String assessmentStage;

    @Column(name = "overall_risk_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal overallRiskScore;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "risk_category", length = 40)
    private String riskCategory;

    @Column(name = "assessment_result", nullable = false, length = 40)
    private String assessmentResult;

    @Column(name = "rules_score", precision = 8, scale = 2)
    private BigDecimal rulesScore;

    @Column(name = "machine_learning_score", precision = 8, scale = 2)
    private BigDecimal machineLearningScore;

    @Column(name = "behavioral_score", precision = 8, scale = 2)
    private BigDecimal behavioralScore;

    @Column(name = "customer_score", precision = 8, scale = 2)
    private BigDecimal customerScore;

    @Column(name = "geographic_score", precision = 8, scale = 2)
    private BigDecimal geographicScore;

    @Column(name = "device_score", precision = 8, scale = 2)
    private BigDecimal deviceScore;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "model_version", length = 40)
    private String modelVersion;

    @Column(name = "assessment_timestamp", nullable = false)
    private LocalDateTime assessmentTimestamp;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "assessment_details", columnDefinition = "jsonb")
    private Map<String, Object> assessmentDetails;

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

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @Version
    @Column(name = "record_version", nullable = false)
    private Integer recordVersion;

    public RiskAssessment() {
    }

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