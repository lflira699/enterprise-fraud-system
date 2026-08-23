package com.efs.modules.playbook.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlaybookExecutionStepRequest {

    private UUID playbookExecutionId;
    private UUID playbookStepId;
    private String status;
    private String result;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public PlaybookExecutionStepRequest() {
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
}