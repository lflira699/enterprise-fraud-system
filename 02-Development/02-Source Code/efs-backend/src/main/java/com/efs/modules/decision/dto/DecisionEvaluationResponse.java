package com.efs.modules.decision.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class DecisionEvaluationResponse {

    private UUID riskAssessmentId;
    private UUID transactionId;
    private String decisionType;
    private BigDecimal confidenceScore;
    private String decisionReason;
    private Boolean finalDecision;

    public DecisionEvaluationResponse() {
    }

    public UUID getRiskAssessmentId() {
        return riskAssessmentId;
    }

    public void setRiskAssessmentId(UUID riskAssessmentId) {
        this.riskAssessmentId = riskAssessmentId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType;
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

    public Boolean getFinalDecision() {
        return finalDecision;
    }

    public void setFinalDecision(Boolean finalDecision) {
        this.finalDecision = finalDecision;
    }
}
