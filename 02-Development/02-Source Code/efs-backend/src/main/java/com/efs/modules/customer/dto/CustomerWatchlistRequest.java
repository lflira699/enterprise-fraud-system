package com.efs.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerWatchlistRequest {

    @NotBlank
    @Size(max = 50)
    private String watchlistType;

    @NotBlank
    @Size(max = 120)
    private String watchlistSource;

    @NotBlank
    @Size(max = 30)
    private String matchStatus;

    private BigDecimal matchScore;

    @Size(max = 250)
    private String matchedName;

    @Size(max = 150)
    private String referenceId;

    private LocalDateTime detectedAt;

    private LocalDateTime lastCheckedAt;

    private Boolean active;

    private UUID createdBy;

    private UUID updatedBy;

    public String getWatchlistType() {
        return watchlistType;
    }

    public void setWatchlistType(String watchlistType) {
        this.watchlistType = watchlistType;
    }

    public String getWatchlistSource() {
        return watchlistSource;
    }

    public void setWatchlistSource(String watchlistSource) {
        this.watchlistSource = watchlistSource;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public BigDecimal getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(BigDecimal matchScore) {
        this.matchScore = matchScore;
    }

    public String getMatchedName() {
        return matchedName;
    }

    public void setMatchedName(String matchedName) {
        this.matchedName = matchedName;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }
}