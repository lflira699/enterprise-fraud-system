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
@Table(name = "playbook_execution_step", schema = "playbook")
public class PlaybookExecutionStep {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "playbook_execution_step_id", nullable = false)
    private UUID playbookExecutionStepId;

    @Column(name = "playbook_execution_id", nullable = false)
    private UUID playbookExecutionId;

    @Column(name = "playbook_step_id", nullable = false)
    private UUID playbookStepId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PlaybookExecutionStep() {
    }

    public UUID getPlaybookExecutionStepId() {
        return playbookExecutionStepId;
    }

    public void setPlaybookExecutionStepId(UUID playbookExecutionStepId) {
        this.playbookExecutionStepId = playbookExecutionStepId;
    }

    public UUID getPlaybookExecutionId() {
        return playbookExecutionId;
    }

    public void setPlaybookExecutionId(UUID playbookExecutionId) {
        this.playbookExecutionId = playbookExecutionId;
    }

    public UUID getPlaybookStepId() {
        return playbookStepId;
    }

    public void setPlaybookStepId(UUID playbookStepId) {
        this.playbookStepId = playbookStepId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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