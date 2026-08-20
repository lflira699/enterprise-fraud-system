package com.efs.modules.detection.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class NetworkAnalysisResponse {

    private UUID networkAnalysisId;
    private UUID customerId;
    private UUID transactionId;
    private UUID correlationId;
    private String analysisStatus;
    private String networkType;
    private String networkKey;
    private Integer entityCount;
    private Integer relationshipCount;
    private BigDecimal networkConfidence;
    private Map<String, Object> networkIndicators;
    private Map<String, Object> analysisContext;
    private LocalDateTime analyzedAt;
    private LocalDateTime createdAt;

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