package com.efs.modules.rules.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "rule_simulation", schema = "rules")
public class RuleSimulation {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "simulation_id", nullable = false)
    private UUID simulationId;

    @Column(name = "simulation_name", nullable = false, length = 180)
    private String simulationName;

    @Column(name = "entity_type", nullable = false, length = 30)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "dataset_reference", nullable = false, length = 250)
    private String datasetReference;

    @Column(name = "sample_size", nullable = false)
    private Long sampleSize;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "simulation_status", nullable = false, length = 30)
    private String simulationStatus;

    @Column(name = "match_count", nullable = false)
    private Long matchCount;

    @Column(name = "approve_count", nullable = false)
    private Long approveCount;

    @Column(name = "reject_count", nullable = false)
    private Long rejectCount;

    @Column(name = "review_count", nullable = false)
    private Long reviewCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result_summary", columnDefinition = "jsonb")
    private Map<String, Object> resultSummary;

    @Column(name = "executed_by", nullable = false)
    private UUID executedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RuleSimulation() {
    }

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