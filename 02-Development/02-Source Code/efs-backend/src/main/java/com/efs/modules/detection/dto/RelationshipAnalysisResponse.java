package com.efs.modules.detection.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class RelationshipAnalysisResponse {

    private UUID relationshipAnalysisId;
    private UUID customerId;
    private UUID transactionId;
    private UUID correlationId;
    private String analysisStatus;
    private String relationshipType;
    private String sourceEntityType;
    private String sourceEntityKey;
    private String targetEntityType;
    private String targetEntityKey;
    private BigDecimal relationshipStrength;
    private Integer entityCount;
    private Integer relationshipCount;
    private Map<String, Object> relationshipIndicators;
    private Map<String, Object> analysisContext;
    private LocalDateTime analyzedAt;
    private LocalDateTime createdAt;

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