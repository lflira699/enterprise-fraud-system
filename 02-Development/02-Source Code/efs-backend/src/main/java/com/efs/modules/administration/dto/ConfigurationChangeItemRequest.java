package com.efs.modules.administration.dto;

public class ConfigurationChangeItemRequest {

    private String configurationKey;
    private String proposedValue;

    public ConfigurationChangeItemRequest() {
    }

    public ConfigurationChangeItemRequest(
            String configurationKey,
            String proposedValue) {

        this.configurationKey =
                configurationKey;

        this.proposedValue =
                proposedValue;
    }

    public String getConfigurationKey() {
        return configurationKey;
    }

    public void setConfigurationKey(
            String configurationKey) {
        this.configurationKey =
                configurationKey;
    }

    public String getProposedValue() {
        return proposedValue;
    }

    public void setProposedValue(
            String proposedValue) {
        this.proposedValue =
                proposedValue;
    }
}