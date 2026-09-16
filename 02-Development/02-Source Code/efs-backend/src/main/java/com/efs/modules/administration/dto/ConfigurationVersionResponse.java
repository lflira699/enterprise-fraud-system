package com.efs.modules.administration.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConfigurationVersionResponse {

    private UUID changeRequestId;
    private String configurationKey;
    private String configurationValue;
    private String configurationType;
    private Integer versionNumber;
    private UUID organizationId;
    private UUID tenantId;
    private Boolean encrypted;
    private UUID appliedBy;
    private LocalDateTime appliedAt;

    public UUID getChangeRequestId() {
        return changeRequestId;
    }

    public void setChangeRequestId(
            UUID changeRequestId) {
        this.changeRequestId =
                changeRequestId;
    }

    public String getConfigurationKey() {
        return configurationKey;
    }

    public void setConfigurationKey(
            String configurationKey) {
        this.configurationKey =
                configurationKey;
    }

    public String getConfigurationValue() {
        return configurationValue;
    }

    public void setConfigurationValue(
            String configurationValue) {
        this.configurationValue =
                configurationValue;
    }

    public String getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(
            String configurationType) {
        this.configurationType =
                configurationType;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(
            Integer versionNumber) {
        this.versionNumber =
                versionNumber;
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

    public Boolean getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(
            Boolean encrypted) {
        this.encrypted =
                encrypted;
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
}