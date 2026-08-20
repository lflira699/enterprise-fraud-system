package com.efs.modules.rules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RuleRequest {

    @NotBlank
    @Size(max = 60)
    private String ruleCode;

    @NotBlank
    @Size(max = 200)
    private String ruleName;

    private String description;

    @NotBlank
    @Size(max = 50)
    private String category;

    @NotBlank
    @Size(max = 20)
    private String severity;

    @NotNull
    private Short priority;

    @Size(max = 100)
    private String ownerTeam;

    @NotNull
    private Integer currentVersion;

    @NotBlank
    @Size(max = 30)
    private String status;

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Short getPriority() {
        return priority;
    }

    public void setPriority(Short priority) {
        this.priority = priority;
    }

    public String getOwnerTeam() {
        return ownerTeam;
    }

    public void setOwnerTeam(String ownerTeam) {
        this.ownerTeam = ownerTeam;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}