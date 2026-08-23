package com.efs.modules.casemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "case_resolution",
        schema = "case_management"
)
public class CaseResolution {

    @Id
    @GeneratedValue
    @Column(
            name = "resolution_id",
            nullable = false
    )
    private UUID resolutionId;

    @Column(
            name = "case_id",
            nullable = false
    )
    private UUID caseId;

    @Column(
            name = "resolution_type",
            nullable = false,
            length = 40
    )
    private String resolutionType;

    @Column(
            name = "resolution_summary",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String resolutionSummary;

    @Column(
            name = "economic_impact",
            precision = 18,
            scale = 2
    )
    private BigDecimal economicImpact;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name = "currency_code",
            length = 3,
            columnDefinition = "CHAR(3)"
    )
    private String currencyCode;

    @Column(
            name = "resolved_by",
            nullable = false
    )
    private UUID resolvedBy;

    @Column(
            name = "resolved_at",
            nullable = false
    )
    private LocalDateTime resolvedAt;

    @Column(
            name = "approved_by"
    )
    private UUID approvedBy;

    public CaseResolution() {
    }

    public UUID getResolutionId() {
        return resolutionId;
    }

    public void setResolutionId(
            UUID resolutionId) {

        this.resolutionId =
                resolutionId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(
            UUID caseId) {

        this.caseId =
                caseId;
    }

    public String getResolutionType() {
        return resolutionType;
    }

    public void setResolutionType(
            String resolutionType) {

        this.resolutionType =
                resolutionType;
    }

    public String getResolutionSummary() {
        return resolutionSummary;
    }

    public void setResolutionSummary(
            String resolutionSummary) {

        this.resolutionSummary =
                resolutionSummary;
    }

    public BigDecimal getEconomicImpact() {
        return economicImpact;
    }

    public void setEconomicImpact(
            BigDecimal economicImpact) {

        this.economicImpact =
                economicImpact;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(
            String currencyCode) {

        this.currencyCode =
                currencyCode;
    }

    public UUID getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(
            UUID resolvedBy) {

        this.resolvedBy =
                resolvedBy;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(
            LocalDateTime resolvedAt) {

        this.resolvedAt =
                resolvedAt;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(
            UUID approvedBy) {

        this.approvedBy =
                approvedBy;
    }
}