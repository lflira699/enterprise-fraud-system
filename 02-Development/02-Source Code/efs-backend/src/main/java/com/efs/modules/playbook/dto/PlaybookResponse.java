package com.efs.modules.playbook.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlaybookResponse {

    private UUID playbookId;
    private String playbookCode;
    private String playbookName;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlaybookResponse() {
    }

    public UUID getPlaybookId() {
        return playbookId;
    }

    public void setPlaybookId(UUID playbookId) {
        this.playbookId = playbookId;
    }

    public String getPlaybookCode() {
        return playbookCode;
    }

    public void setPlaybookCode(String playbookCode) {
        this.playbookCode = playbookCode;
    }

    public String getPlaybookName() {
        return playbookName;
    }

    public void setPlaybookName(String playbookName) {
        this.playbookName = playbookName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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