package com.efs.modules.playbook.dto;

public class PlaybookRequest {

    private String playbookCode;
    private String playbookName;
    private String description;
    private String status;

    public PlaybookRequest() {
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
}