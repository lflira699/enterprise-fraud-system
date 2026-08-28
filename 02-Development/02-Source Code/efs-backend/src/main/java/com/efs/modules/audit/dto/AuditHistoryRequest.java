package com.efs.modules.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class AuditHistoryRequest {

    @NotBlank
    @Size(max = 80)
    private String sourceTable;

    @NotNull
    private UUID sourceRecordId;

    private UUID organizationId;

    private UUID tenantId;

    private UUID correlationId;

    @NotNull
    private LocalDateTime eventTimestamp;

    @NotNull
    private Map<String, Object> archivedPayload;

    @NotBlank
    @Size(min = 64, max = 64)
    private String checksumSha256;

    private LocalDateTime retentionUntil;

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public UUID getSourceRecordId() {
        return sourceRecordId;
    }

    public void setSourceRecordId(UUID sourceRecordId) {
        this.sourceRecordId = sourceRecordId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public Map<String, Object> getArchivedPayload() {
        return archivedPayload;
    }

    public void setArchivedPayload(Map<String, Object> archivedPayload) {
        this.archivedPayload = archivedPayload;
    }

    public String getChecksumSha256() {
        return checksumSha256;
    }

    public void setChecksumSha256(String checksumSha256) {
        this.checksumSha256 = checksumSha256;
    }

    public LocalDateTime getRetentionUntil() {
        return retentionUntil;
    }

    public void setRetentionUntil(LocalDateTime retentionUntil) {
        this.retentionUntil = retentionUntil;
    }
}