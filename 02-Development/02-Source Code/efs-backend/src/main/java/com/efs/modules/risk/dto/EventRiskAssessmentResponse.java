package com.efs.modules.risk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class EventRiskAssessmentResponse {

    private UUID eventRiskAssessmentId;
    private UUID fraudEventId;
    private UUID organizationId;
    private UUID tenantId;
    private UUID correlationId;

    private String modelId;
    private String modelVersion;

    private BigDecimal overallRiskScore;
    private String riskLevel;
    private String riskCategory;
    private String assessmentResult;

    private Map<String, Object> assessmentDetails;

    private LocalDateTime assessmentTimestamp;
    private Long processingTimeMs;

    public UUID getEventRiskAssessmentId() {
        return eventRiskAssessmentId;
    }

    public void setEventRiskAssessmentId(
            UUID eventRiskAssessmentId) {

        this.eventRiskAssessmentId =
                eventRiskAssessmentId;
    }

    public UUID getFraudEventId() {
        return fraudEventId;
    }

    public void setFraudEventId(
            UUID fraudEventId) {

        this.fraudEventId =
                fraudEventId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(
            UUID organizationId) {

        this.organizationId =
                organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            UUID tenantId) {

        this.tenantId =
                tenantId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(
            UUID correlationId) {

        this.correlationId =
                correlationId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(
            String modelId) {

        this.modelId =
                modelId;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(
            String modelVersion) {

        this.modelVersion =
                modelVersion;
    }

    public BigDecimal getOverallRiskScore() {
        return overallRiskScore;
    }

    public void setOverallRiskScore(
            BigDecimal overallRiskScore) {

        this.overallRiskScore =
                overallRiskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(
            String riskLevel) {

        this.riskLevel =
                riskLevel;
    }

    public String getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(
            String riskCategory) {

        this.riskCategory =
                riskCategory;
    }

    public String getAssessmentResult() {
        return assessmentResult;
    }

    public void setAssessmentResult(
            String assessmentResult) {

        this.assessmentResult =
                assessmentResult;
    }

    public Map<String, Object> getAssessmentDetails() {
        return assessmentDetails;
    }

    public void setAssessmentDetails(
            Map<String, Object> assessmentDetails) {

        this.assessmentDetails =
                assessmentDetails;
    }

    public LocalDateTime getAssessmentTimestamp() {
        return assessmentTimestamp;
    }

    public void setAssessmentTimestamp(
            LocalDateTime assessmentTimestamp) {

        this.assessmentTimestamp =
                assessmentTimestamp;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(
            Long processingTimeMs) {

        this.processingTimeMs =
                processingTimeMs;
    }
}
