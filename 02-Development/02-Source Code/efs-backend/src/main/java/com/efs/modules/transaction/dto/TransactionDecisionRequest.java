package com.efs.modules.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionDecisionRequest {

    @NotBlank
    @Size(max = 40)
    private String decisionType;

    @NotBlank
    @Size(max = 40)
    private String decisionSource;

    private BigDecimal confidenceScore;

    private String decisionReason;

    private UUID approvedBy;

    @NotNull
    private LocalDateTime decisionTimestamp;

    @NotNull
    private Boolean finalDecision;

    public String getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType;
    }

    public String getDecisionSource() {
        return decisionSource;
    }

    public void setDecisionSource(String decisionSource) {
        this.decisionSource = decisionSource;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getDecisionTimestamp() {
        return decisionTimestamp;
    }

    public void setDecisionTimestamp(LocalDateTime decisionTimestamp) {
        this.decisionTimestamp = decisionTimestamp;
    }

    public Boolean getFinalDecision() {
        return finalDecision;
    }

    public void setFinalDecision(Boolean finalDecision) {
        this.finalDecision = finalDecision;
    }
}