package com.efs.modules.rules.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class RuleSimulationResponse {

    private UUID simulationId;
    private String simulationName;
    private String entityType;
    private UUID entityId;
    private String datasetReference;
    private Long sampleSize;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String simulationStatus;
    private Long matchCount;
    private Long approveCount;
    private Long rejectCount;
    private Long reviewCount;
    private Map<String, Object> resultSummary;
    private UUID executedBy;
    private LocalDateTime createdAt;

    public UUID getSimulationId() {
        return simulationId;
    }

    public void setSimulationId(UUID simulationId) {
        this.simulationId = simulationId;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getDatasetReference() {
        return datasetReference;
    }

    public void setDatasetReference(String datasetReference) {
        this.datasetReference = datasetReference;
    }

    public Long getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Long sampleSize) {
        this.sampleSize = sampleSize;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getSimulationStatus() {
        return simulationStatus;
    }

    public void setSimulationStatus(String simulationStatus) {
        this.simulationStatus = simulationStatus;
    }

    public Long getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(Long matchCount) {
        this.matchCount = matchCount;
    }

    public Long getApproveCount() {
        return approveCount;
    }

    public void setApproveCount(Long approveCount) {
        this.approveCount = approveCount;
    }

    public Long getRejectCount() {
        return rejectCount;
    }

    public void setRejectCount(Long rejectCount) {
        this.rejectCount = rejectCount;
    }

    public Long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Long reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Map<String, Object> getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(Map<String, Object> resultSummary) {
        this.resultSummary = resultSummary;
    }

    public UUID getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(UUID executedBy) {
        this.executedBy = executedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}