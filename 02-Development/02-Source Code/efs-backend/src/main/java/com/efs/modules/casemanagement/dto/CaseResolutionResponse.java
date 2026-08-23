package com.efs.modules.casemanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CaseResolutionResponse {

    private UUID resolutionId;
    private UUID caseId;
    private String resolutionType;
    private String resolutionSummary;
    private BigDecimal economicImpact;
    private String currencyCode;
    private UUID resolvedBy;
    private LocalDateTime resolvedAt;
    private UUID approvedBy;

    public UUID getResolutionId() {
        return resolutionId;
    }

    public void setResolutionId(UUID resolutionId) {
        this.resolutionId = resolutionId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }

    public String getResolutionType() {
        return resolutionType;
    }

    public void setResolutionType(String resolutionType) {
        this.resolutionType = resolutionType;
    }

    public String getResolutionSummary() {
        return resolutionSummary;
    }

    public void setResolutionSummary(String resolutionSummary) {
        this.resolutionSummary = resolutionSummary;
    }

    public BigDecimal getEconomicImpact() {
        return economicImpact;
    }

    public void setEconomicImpact(BigDecimal economicImpact) {
        this.economicImpact = economicImpact;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public UUID getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(UUID resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
    }
}