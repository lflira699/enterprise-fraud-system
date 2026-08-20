package com.efs.modules.rules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RuleGroupRequest {

    @NotBlank
    @Size(max = 60)
    private String groupCode;

    @NotBlank
    @Size(max = 150)
    private String groupName;

    private String description;

    @Size(max = 50)
    private String category;

    @NotBlank
    @Size(max = 20)
    private String status;

    private Short executionOrder;

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
}