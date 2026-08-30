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
@Table(name = "relationship_analysis", schema = "detection")
public class RelationshipAnalysis {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "relationship_analysis_id", nullable = false)
    private UUID relationshipAnalysisId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "analysis_status", nullable = false, length = 30)
    private String analysisStatus;

    @Column(name = "relationship_type", nullable = false, length = 50)
    private String relationshipType;

    @Column(name = "source_entity_type", nullable = false, length = 40)
    private String sourceEntityType;

    @Column(name = "source_entity_key", nullable = false, length = 180)
    private String sourceEntityKey;

    @Column(name = "target_entity_type", nullable = false, length = 40)
    private String targetEntityType;

    @Column(name = "target_entity_key", nullable = false, length = 180)
    private String targetEntityKey;

    @Column(name = "relationship_strength", precision = 8, scale = 4)
    private BigDecimal relationshipStrength;

    @Column(name = "entity_count", nullable = false)
    private Integer entityCount = 0;

    @Column(name = "relationship_count", nullable = false)
    private Integer relationshipCount = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "relationship_indicators", columnDefinition = "jsonb")
    private Map<String, Object> relationshipIndicators;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_context", columnDefinition = "jsonb")
    private Map<String, Object> analysisContext;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RelationshipAnalysis() {
    }

    public UUID getRelationshipAnalysisId() {
        return relationshipAnalysisId;
    }

    public void setRelationshipAnalysisId(
            UUID relationshipAnalysisId) {
        this.relationshipAnalysisId = relationshipAnalysisId;
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

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getSourceEntityType() {
        return sourceEntityType;
    }

    public void setSourceEntityType(String sourceEntityType) {
        this.sourceEntityType = sourceEntityType;
    }

    public String getSourceEntityKey() {
        return sourceEntityKey;
    }

    public void setSourceEntityKey(String sourceEntityKey) {
        this.sourceEntityKey = sourceEntityKey;
    }

    public String getTargetEntityType() {
        return targetEntityType;
    }

    public void setTargetEntityType(String targetEntityType) {
        this.targetEntityType = targetEntityType;
    }

    public String getTargetEntityKey() {
        return targetEntityKey;
    }

    public void setTargetEntityKey(String targetEntityKey) {
        this.targetEntityKey = targetEntityKey;
    }

    public BigDecimal getRelationshipStrength() {
        return relationshipStrength;
    }

    public void setRelationshipStrength(
            BigDecimal relationshipStrength) {
        this.relationshipStrength = relationshipStrength;
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

    public Map<String, Object> getRelationshipIndicators() {
        return relationshipIndicators;
    }

    public void setRelationshipIndicators(
            Map<String, Object> relationshipIndicators) {
        this.relationshipIndicators = relationshipIndicators;
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