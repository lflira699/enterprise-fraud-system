package com.efs.modules.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public class CaseResolutionRequest {

    @NotBlank
    @Size(max = 40)
    private String resolutionType;

    @NotBlank
    private String resolutionSummary;

    private BigDecimal economicImpact;

    @Size(max = 3)
    private String currencyCode;

    @NotNull
    private UUID resolvedBy;

    private UUID approvedBy;

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

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
    }
}