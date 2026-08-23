package com.efs.modules.playbook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "playbook_execution", schema = "playbook")
public class PlaybookExecution {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "playbook_execution_id", nullable = false)
    private UUID playbookExecutionId;

    @Column(name = "playbook_version_id", nullable = false)
    private UUID playbookVersionId;

    @Column(name = "alert_id")
    private UUID alertId;

    @Column(name = "scenario_id")
    private UUID scenarioId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PlaybookExecution() {
    }

    public UUID getPlaybookExecutionId() {
        return playbookExecutionId;
    }

    public void setPlaybookExecutionId(UUID playbookExecutionId) {
        this.playbookExecutionId = playbookExecutionId;
    }

    public UUID getPlaybookVersionId() {
        return playbookVersionId;
    }

    public void setPlaybookVersionId(UUID playbookVersionId) {
        this.playbookVersionId = playbookVersionId;
    }

    public UUID getAlertId() {
        return alertId;
    }

    public void setAlertId(UUID alertId) {
        this.alertId = alertId;
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(UUID scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}