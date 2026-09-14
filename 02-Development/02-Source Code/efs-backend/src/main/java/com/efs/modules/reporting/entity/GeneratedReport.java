package com.efs.modules.reporting.entity;

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
@Table(
        name = "generated_report",
        schema = "reporting"
)
public class GeneratedReport {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(
            name = "report_id",
            nullable = false
    )
    private UUID reportId;

    @Column(
            name = "organization_id",
            nullable = false
    )
    private UUID organizationId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(
            name = "report_code",
            nullable = false,
            length = 80
    )
    private String reportCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "criteria",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, Object> criteria;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "content",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, Object> content;

    @Column(
            name = "generated_by",
            nullable = false
    )
    private UUID generatedBy;

    @Column(
            name = "generated_at",
            nullable = false
    )
    private LocalDateTime generatedAt;

    public GeneratedReport() {
    }

    public GeneratedReport(
            UUID organizationId,
            UUID tenantId,
            String reportCode,
            Map<String, Object> criteria,
            Map<String, Object> content,
            UUID generatedBy,
            LocalDateTime generatedAt) {

        this.organizationId =
                organizationId;

        this.tenantId =
                tenantId;

        this.reportCode =
                reportCode;

        this.criteria =
                criteria;

        this.content =
                content;

        this.generatedBy =
                generatedBy;

        this.generatedAt =
                generatedAt;
    }

    @PrePersist
    public void prePersist() {

        if (generatedAt == null) {
            generatedAt =
                    LocalDateTime.now();
        }
    }

    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(
            UUID reportId) {

        this.reportId = reportId;
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
