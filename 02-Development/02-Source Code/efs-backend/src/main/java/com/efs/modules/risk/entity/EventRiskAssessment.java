package com.efs.modules.risk.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "event_risk_assessment",
        schema = "risk"
)
public class EventRiskAssessment {

    @Id
    @Generated(event = EventType.INSERT)
    @Column(
            name = "event_risk_assessment_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private UUID eventRiskAssessmentId;

    @Column(
            name = "fraud_event_id",
            nullable = false,
            updatable = false
    )
    private UUID fraudEventId;

    @Column(
            name = "organization_id",
            nullable = false,
            updatable = false
    )
    private UUID organizationId;

    @Column(
            name = "tenant_id",
            nullable = false,
            updatable = false
    )
    private UUID tenantId;

    @Column(
            name = "correlation_id",
            nullable = false,
            updatable = false
    )
    private UUID correlationId;

    @Column(
            name = "model_id",
            nullable = false,
            length = 100,
            updatable = false
    )
    private String modelId;

    @Column(
            name = "model_version",
            nullable = false,
            length = 40,
            updatable = false
    )
    private String modelVersion;

    @Column(
            name = "overall_risk_score",
            precision = 8,
            scale = 2,
            updatable = false
    )
    private BigDecimal overallRiskScore;

    @Column(
            name = "risk_level",
            length = 20,
            updatable = false
    )
    private String riskLevel;

    @Column(
            name = "risk_category",
            length = 40,
            updatable = false
    )
    private String riskCategory;

    @Column(
            name = "assessment_result",
            length = 40,
            updatable = false
    )
    private String assessmentResult;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "assessment_details",
            columnDefinition = "jsonb",
            updatable = false
    )
    private Map<String, Object> assessmentDetails;

    @Generated(event = EventType.INSERT)
    @Column(
            name = "assessment_timestamp",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime assessmentTimestamp;

    @Column(
            name = "processing_time_ms",
            updatable = false
    )
    private Long processingTimeMs;

    protected EventRiskAssessment() {
    }

    public EventRiskAssessment(
            UUID fraudEventId,
            UUID organizationId,
            UUID tenantId,
            UUID correlationId,
            String modelId,
            String modelVersion,
            BigDecimal overallRiskScore,
            String riskLevel,
            String riskCategory,
            String assessmentResult,
            Map<String, Object> assessmentDetails,
            Long processingTimeMs) {

        this.fraudEventId = fraudEventId;
        this.organizationId = organizationId;
        this.tenantId = tenantId;
        this.correlationId = correlationId;
        this.modelId = modelId;
        this.modelVersion = modelVersion;
        this.overallRiskScore = overallRiskScore;
        this.riskLevel = riskLevel;
        this.riskCategory = riskCategory;
        this.assessmentResult = assessmentResult;
        this.assessmentDetails =
                assessmentDetails == null
                        ? null
                        : Map.copyOf(assessmentDetails);
        this.processingTimeMs = processingTimeMs;
    }

    public UUID getEventRiskAssessmentId() {
        return eventRiskAssessmentId;
    }

    public UUID getFraudEventId() {
        return fraudEventId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public String getModelId() {
        return modelId;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public BigDecimal getOverallRiskScore() {
        return overallRiskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getRiskCategory() {
        return riskCategory;
    }

    public String getAssessmentResult() {
        return assessmentResult;
    }

    public Map<String, Object> getAssessmentDetails() {
        return assessmentDetails;
    }

    public LocalDateTime getAssessmentTimestamp() {
        return assessmentTimestamp;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }
}
