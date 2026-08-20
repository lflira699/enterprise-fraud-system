package com.efs.modules.rules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class RuleSimulationRequest {

    @NotBlank
    @Size(max = 180)
    private String simulationName;

    @NotBlank
    @Size(max = 30)
    private String entityType;

    @NotNull
    private UUID entityId;

    @NotBlank
    @Size(max = 250)
    private String datasetReference;

    @NotNull
    private Long sampleSize;

    @NotBlank
    @Size(max = 30)
    private String simulationStatus;

    @NotNull
    private Long matchCount;

    @NotNull
    private Long approveCount;

    @NotNull
    private Long rejectCount;

    @NotNull
    private Long reviewCount;

    private Object resultSummary;

    @NotNull
    private UUID executedBy;

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

    public Object getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(Object resultSummary) {
        this.resultSummary = resultSummary;
    }

    public UUID getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(UUID executedBy) {
        this.executedBy = executedBy;
    }
}