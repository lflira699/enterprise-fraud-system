package com.efs.modules.catalog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CurrencyResponse {

    private UUID currencyId;
    private String currencyCode;
    private String numericCode;
    private String currencyName;
    private Short minorUnit;
    private String status;
    private LocalDateTime createdAt;

    public UUID getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(UUID currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getNumericCode() {
        return numericCode;
    }

    public void setNumericCode(String numericCode) {
        this.numericCode = numericCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Short getMinorUnit() {
        return minorUnit;
    }

    public void setMinorUnit(Short minorUnit) {
        this.minorUnit = minorUnit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}