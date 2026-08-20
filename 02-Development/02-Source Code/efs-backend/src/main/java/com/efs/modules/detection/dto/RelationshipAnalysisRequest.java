package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public class RelationshipAnalysisRequest {

    private UUID customerId;

    private UUID transactionId;

    private UUID correlationId;

    @NotBlank
    @Size(max = 30)
    private String analysisStatus;

    @NotBlank
    @Size(max = 50)
    private String relationshipType;

    @NotBlank
    @Size(max = 40)
    private String sourceEntityType;

    @NotBlank
    @Size(max = 180)
    private String sourceEntityKey;

    @NotBlank
    @Size(max = 40)
    private String targetEntityType;

    @NotBlank
    @Size(max = 180)
    private String targetEntityKey;

    private Map<String, Object> relationshipIndicators;

    private Map<String, Object> analysisContext;

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
}