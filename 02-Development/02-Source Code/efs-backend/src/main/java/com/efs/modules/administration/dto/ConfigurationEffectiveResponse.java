package com.efs.modules.administration.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConfigurationEffectiveResponse {

    private String configurationKey;
    private String configurationValue;
    private String configurationType;
    private String effectiveScope;
    private UUID organizationId;
    private UUID tenantId;
    private Boolean encrypted;
    private Boolean critical;
    private UUID updatedBy;
    private LocalDateTime updatedAt;

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

    public String getEffectiveScope() {
        return effectiveScope;
    }

    public void setEffectiveScope(
            String effectiveScope) {
        this.effectiveScope =
                effectiveScope;
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

    public Boolean getCritical() {
        return critical;
    }

    public void setCritical(
            Boolean critical) {
        this.critical =
                critical;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(
            UUID updatedBy) {
        this.updatedBy =
                updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {
        this.updatedAt =
                updatedAt;
    }
}