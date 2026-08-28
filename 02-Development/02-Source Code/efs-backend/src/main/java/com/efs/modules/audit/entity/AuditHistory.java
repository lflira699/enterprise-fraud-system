package com.efs.modules.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "audit_history", schema = "audit")
public class AuditHistory {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "history_id", nullable = false)
    private UUID historyId;

    @Column(name = "source_table", nullable = false, length = 80)
    private String sourceTable;

    @Column(name = "source_record_id", nullable = false)
    private UUID sourceRecordId;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "archived_payload",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, Object> archivedPayload;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name = "checksum_sha256",
            nullable = false,
            length = 64,
            columnDefinition = "char(64)"
    )
    private String checksumSha256;

    @Column(name = "archived_at", nullable = false)
    private LocalDateTime archivedAt;

    @Column(name = "retention_until")
    private LocalDateTime retentionUntil;

    public AuditHistory() {
    }

    @PrePersist
    public void prePersist() {

        if (archivedAt == null) {
            archivedAt = LocalDateTime.now();
        }
    }

    public UUID getHistoryId() {
        return historyId;
    }

    public void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }

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

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public LocalDateTime getRetentionUntil() {
        return retentionUntil;
    }

    public void setRetentionUntil(LocalDateTime retentionUntil) {
        this.retentionUntil = retentionUntil;
    }
}