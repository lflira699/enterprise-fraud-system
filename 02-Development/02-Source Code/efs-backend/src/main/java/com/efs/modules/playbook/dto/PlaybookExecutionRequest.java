package com.efs.modules.playbook.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlaybookExecutionRequest {

    private UUID playbookVersionId;
    private UUID alertId;
    private UUID scenarioId;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public PlaybookExecutionRequest() {
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
}