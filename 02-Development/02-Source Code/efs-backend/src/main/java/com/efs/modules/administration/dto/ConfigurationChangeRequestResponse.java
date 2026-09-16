package com.efs.modules.administration.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ConfigurationChangeRequestResponse {

    private UUID changeRequestId;
    private UUID organizationId;
    private UUID tenantId;
    private UUID requestedBy;
    private String status;
    private String justification;
    private String affectedEnvironment;
    private String riskAssessment;
    private String expectedResult;
    private String rollbackPlan;
    private LocalDateTime requestedAt;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
    private UUID rejectedBy;
    private LocalDateTime rejectedAt;
    private String rejectionReason;
    private UUID appliedBy;
    private LocalDateTime appliedAt;
    private String failureReason;
    private LocalDateTime updatedAt;
    private List<Item> items;

    public static class Item {

        private UUID changeItemId;
        private String configurationKey;
        private String previousValue;
        private String proposedValue;
        private String configurationType;
        private Boolean encrypted;
        private Integer versionNumber;
        private LocalDateTime createdAt;

        public UUID getChangeItemId() {
            return changeItemId;
        }

        public void setChangeItemId(
                UUID changeItemId) {
            this.changeItemId =
                    changeItemId;
        }

        public String getConfigurationKey() {
            return configurationKey;
        }

        public void setConfigurationKey(
                String configurationKey) {
            this.configurationKey =
                    configurationKey;
        }

        public String getPreviousValue() {
            return previousValue;
        }

        public void setPreviousValue(
                String previousValue) {
            this.previousValue =
                    previousValue;
        }

        public String getProposedValue() {
            return proposedValue;
        }

        public void setProposedValue(
                String proposedValue) {
            this.proposedValue =
                    proposedValue;
        }

        public String getConfigurationType() {
            return configurationType;
        }

        public void setConfigurationType(
                String configurationType) {
            this.configurationType =
                    configurationType;
        }

        public Boolean getEncrypted() {
            return encrypted;
        }

        public void setEncrypted(
                Boolean encrypted) {
            this.encrypted =
                    encrypted;
        }

        public Integer getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(
                Integer versionNumber) {
            this.versionNumber =
                    versionNumber;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(
                LocalDateTime createdAt) {
            this.createdAt =
                    createdAt;
        }
    }

    public UUID getChangeRequestId() {
        return changeRequestId;
    }

    public void setChangeRequestId(
            UUID changeRequestId) {
        this.changeRequestId =
                changeRequestId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(
            UUID organizationId) {
        this.organizationId =
                organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            UUID tenantId) {
        this.tenantId =
                tenantId;
    }

    public UUID getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(
            UUID requestedBy) {
        this.requestedBy =
                requestedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status =
                status;
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

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(
            LocalDateTime requestedAt) {
        this.requestedAt =
                requestedAt;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(
            UUID approvedBy) {
        this.approvedBy =
                approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(
            LocalDateTime approvedAt) {
        this.approvedAt =
                approvedAt;
    }

    public UUID getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(
            UUID rejectedBy) {
        this.rejectedBy =
                rejectedBy;
    }

    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(
            LocalDateTime rejectedAt) {
        this.rejectedAt =
                rejectedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(
            String rejectionReason) {
        this.rejectionReason =
                rejectionReason;
    }

    public UUID getAppliedBy() {
        return appliedBy;
    }

    public void setAppliedBy(
            UUID appliedBy) {
        this.appliedBy =
                appliedBy;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(
            LocalDateTime appliedAt) {
        this.appliedAt =
                appliedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(
            String failureReason) {
        this.failureReason =
                failureReason;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {
        this.updatedAt =
                updatedAt;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(
            List<Item> items) {
        this.items =
                items;
    }
}