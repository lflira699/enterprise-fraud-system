package com.efs.modules.rules.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class RuleGroupResponse {

    private UUID ruleGroupId;
    private String groupCode;
    private String groupName;
    private String description;
    private String category;
    private String status;
    private Short executionOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(UUID ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Short getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(Short executionOrder) {
        this.executionOrder = executionOrder;
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