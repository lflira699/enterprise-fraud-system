package com.efs.modules.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionDecisionResponse {

    private UUID decisionId;
    private UUID transactionId;
    private UUID riskAssessmentId;
    private String decisionType;
    private String decisionSource;
    private BigDecimal confidenceScore;
    private String decisionReason;
    private UUID approvedBy;
    private LocalDateTime decisionTimestamp;
    private Boolean finalDecision;

    public UUID getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(UUID decisionId) {
        this.decisionId = decisionId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getRiskAssessmentId() {
        return riskAssessmentId;
    }

    public void setRiskAssessmentId(UUID riskAssessmentId) {
        this.riskAssessmentId = riskAssessmentId;
    }

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