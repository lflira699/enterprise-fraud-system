package com.efs.modules.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_export", schema = "audit")
public class AuditExport {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "export_id", nullable = false)
    private UUID exportId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "export_type", nullable = false, length = 40)
    private String exportType;

    @Column(name = "resource_type", nullable = false, length = 60)
    private String resourceType;

    @Column(name = "resource_id")
    private UUID resourceId;

    @Column(name = "file_format", nullable = false, length = 20)
    private String fileFormat;

    @Column(name = "record_count", nullable = false)
    private Long recordCount;

    @Column(name = "export_reason", columnDefinition = "TEXT")
    private String exportReason;

    @Column(name = "exported_at", nullable = false)
    private LocalDateTime exportedAt;

    public AuditExport() {
    }

    @PrePersist
    public void prePersist() {

        if (exportedAt == null) {
            exportedAt = LocalDateTime.now();
        }
    }

    public UUID getExportId() {
        return exportId;
    }

    public void setExportId(UUID exportId) {
        this.exportId = exportId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    public String getExportReason() {
        return exportReason;
    }

    public void setExportReason(String exportReason) {
        this.exportReason = exportReason;
    }

    public LocalDateTime getExportedAt() {
        return exportedAt;
    }

    public void setExportedAt(LocalDateTime exportedAt) {
        this.exportedAt = exportedAt;
    }
}