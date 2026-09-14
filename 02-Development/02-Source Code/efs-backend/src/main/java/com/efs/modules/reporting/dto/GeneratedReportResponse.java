package com.efs.modules.reporting.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class GeneratedReportResponse {

    private final UUID reportId;

    private final UUID organizationId;

    private final UUID tenantId;

    private final String reportCode;

    private final Map<String, Object> criteria;

    private final Map<String, Object> content;

    private final UUID generatedBy;

    private final LocalDateTime generatedAt;

    public GeneratedReportResponse(
            UUID reportId,
            UUID organizationId,
            UUID tenantId,
            String reportCode,
            Map<String, Object> criteria,
            Map<String, Object> content,
            UUID generatedBy,
            LocalDateTime generatedAt) {

        this.reportId = reportId;
        this.organizationId = organizationId;
        this.tenantId = tenantId;
        this.reportCode = reportCode;
        this.criteria = criteria;
        this.content = content;
        this.generatedBy = generatedBy;
        this.generatedAt = generatedAt;
    }

    public UUID getReportId() {
        return reportId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getReportCode() {
        return reportCode;
    }

    public Map<String, Object> getCriteria() {
        return criteria;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public UUID getGeneratedBy() {
        return generatedBy;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
}
