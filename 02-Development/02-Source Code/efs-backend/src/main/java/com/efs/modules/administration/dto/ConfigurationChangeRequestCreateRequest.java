package com.efs.modules.administration.dto;

import java.util.List;
import java.util.UUID;

public class ConfigurationChangeRequestCreateRequest {

    private UUID tenantId;
    private String justification;
    private String affectedEnvironment;
    private String riskAssessment;
    private String expectedResult;
    private String rollbackPlan;
    private List<ConfigurationChangeItemRequest> items;

    public ConfigurationChangeRequestCreateRequest() {
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            UUID tenantId) {
        this.tenantId =
                tenantId;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(
            String justification) {
        this.justification =
                justification;
    }

    public String getAffectedEnvironment() {
        return affectedEnvironment;
    }

    public void setAffectedEnvironment(
            String affectedEnvironment) {
        this.affectedEnvironment =
                affectedEnvironment;
    }

    public String getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(
            String riskAssessment) {
        this.riskAssessment =
                riskAssessment;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(
            String expectedResult) {
        this.expectedResult =
                expectedResult;
    }

    public String getRollbackPlan() {
        return rollbackPlan;
    }

    public void setRollbackPlan(
            String rollbackPlan) {
        this.rollbackPlan =
                rollbackPlan;
    }

    public List<ConfigurationChangeItemRequest>
    getItems() {
        return items;
    }

    public void setItems(
            List<ConfigurationChangeItemRequest> items) {
        this.items =
                items;
    }
}