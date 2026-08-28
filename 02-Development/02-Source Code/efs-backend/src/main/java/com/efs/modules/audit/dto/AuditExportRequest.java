package com.efs.modules.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class AuditExportRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID organizationId;

    @NotBlank
    @Size(max = 40)
    private String exportType;

    @NotBlank
    @Size(max = 60)
    private String resourceType;

    private UUID resourceId;

    @NotBlank
    @Size(max = 20)
    private String fileFormat;

    @NotNull
    private Long recordCount;

    private String exportReason;

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
}