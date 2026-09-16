package com.efs.modules.administration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "configuration_change_item",
        schema = "administration"
)
public class ConfigurationChangeItem {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(
            name = "change_item_id",
            nullable = false
    )
    private UUID changeItemId;

    @Column(
            name = "change_request_id",
            nullable = false
    )
    private UUID changeRequestId;

    @Column(
            name = "configuration_key",
            nullable = false,
            length = 150
    )
    private String configurationKey;

    @Column(
            name = "previous_value",
            columnDefinition = "TEXT"
    )
    private String previousValue;

    @Column(
            name = "proposed_value",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String proposedValue;

    @Column(
            name = "configuration_type",
            nullable = false,
            length = 50
    )
    private String configurationType;

    @Column(
            name = "encrypted",
            nullable = false
    )
    private Boolean encrypted;

    @Column(name = "version_number")
    private Integer versionNumber;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public ConfigurationChangeItem() {
    }

    public UUID getChangeItemId() {
        return changeItemId;
    }

    public void setChangeItemId(
            UUID changeItemId) {
        this.changeItemId =
                changeItemId;
    }

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