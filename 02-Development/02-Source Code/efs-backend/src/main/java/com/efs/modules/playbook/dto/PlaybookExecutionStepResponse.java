package com.efs.modules.playbook.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlaybookExecutionStepResponse {

    private UUID playbookExecutionStepId;
    private UUID playbookExecutionId;
    private UUID playbookStepId;
    private String status;
    private String result;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlaybookExecutionStepResponse() {
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