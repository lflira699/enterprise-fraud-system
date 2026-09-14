package com.efs.modules.reporting.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReportCriteriaRequest {

    private UUID tenantId;

    private List<String> components;

    private String status;

    private String priority;

    private UUID assignedUser;

    private String assignedTeam;

    private final Map<String, Object>
            unsupportedCriteria =
            new LinkedHashMap<>();

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(
            List<String> components) {

        this.components = components;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public UUID getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(
            UUID assignedUser) {

        this.assignedUser = assignedUser;
    }

    public String getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(
            String assignedTeam) {

        this.assignedTeam = assignedTeam;
    }

    @JsonAnySetter
    public void captureUnsupportedCriterion(
            String name,
            Object value) {

        unsupportedCriteria.put(
                name,
                value
        );
    }

    @JsonIgnore
    public Map<String, Object>
    getUnsupportedCriteria() {

        return unsupportedCriteria;
    }
}
