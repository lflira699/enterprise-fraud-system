package com.efs.modules.detection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "network_analysis", schema = "detection")
public class NetworkAnalysis {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "network_analysis_id", nullable = false)
    private UUID networkAnalysisId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "analysis_status", nullable = false, length = 30)
    private String analysisStatus;

    @Column(name = "network_type", nullable = false, length = 40)
    private String networkType;

    @Column(name = "network_key", length = 120)
    private String networkKey;

    @Column(name = "entity_count", nullable = false)
    private Integer entityCount;

    @Column(name = "relationship_count", nullable = false)
    private Integer relationshipCount;

    @Column(name = "network_confidence", precision = 8, scale = 4)
    private BigDecimal networkConfidence;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "network_indicators", columnDefinition = "jsonb")
    private Map<String, Object> networkIndicators;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_context", columnDefinition = "jsonb")
    private Map<String, Object> analysisContext;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public NetworkAnalysis() {
    }

    public UUID getNetworkAnalysisId() {
        return networkAnalysisId;
    }

    public void setNetworkAnalysisId(UUID networkAnalysisId) {
        this.networkAnalysisId = networkAnalysisId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public String getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(String analysisStatus) {
        this.analysisStatus = analysisStatus;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getNetworkKey() {
        return networkKey;
    }

    public void setNetworkKey(String networkKey) {
        this.networkKey = networkKey;
    }

    public Integer getEntityCount() {
        return entityCount;
    }

    public void setEntityCount(Integer entityCount) {
        this.entityCount = entityCount;
    }

    public Integer getRelationshipCount() {
        return relationshipCount;
    }

    public void setRelationshipCount(Integer relationshipCount) {
        this.relationshipCount = relationshipCount;
    }

    public BigDecimal getNetworkConfidence() {
        return networkConfidence;
    }

    public void setNetworkConfidence(BigDecimal networkConfidence) {
        this.networkConfidence = networkConfidence;
    }

    public Map<String, Object> getNetworkIndicators() {
        return networkIndicators;
    }

    public void setNetworkIndicators(
            Map<String, Object> networkIndicators) {
        this.networkIndicators = networkIndicators;
    }

    public Map<String, Object> getAnalysisContext() {
        return analysisContext;
    }

    public void setAnalysisContext(
            Map<String, Object> analysisContext) {
        this.analysisContext = analysisContext;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}