package com.efs.modules.playbook.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlaybookStepResponse {

    private UUID playbookStepId;
    private UUID playbookVersionId;
    private Integer stepOrder;
    private String stepName;
    private String description;
    private String expectedResult;
    private Integer expectedDurationMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlaybookStepResponse() {
    }

    public UUID getPlaybookStepId() {
        return playbookStepId;
    }

    public void setPlaybookStepId(UUID playbookStepId) {
        this.playbookStepId = playbookStepId;
    }

    public UUID getPlaybookVersionId() {
        return playbookVersionId;
    }

    public void setPlaybookVersionId(UUID playbookVersionId) {
        this.playbookVersionId = playbookVersionId;
    }

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public Integer getExpectedDurationMinutes() {
        return expectedDurationMinutes;
    }

    public void setExpectedDurationMinutes(Integer expectedDurationMinutes) {
        this.expectedDurationMinutes = expectedDurationMinutes;
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