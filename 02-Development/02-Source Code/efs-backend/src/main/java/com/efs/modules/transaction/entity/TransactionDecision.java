package com.efs.modules.transaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_decision", schema = "transaction")
public class TransactionDecision {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "decision_id", nullable = false)
    private UUID decisionId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "risk_assessment_id")
    private UUID riskAssessmentId;

    @Column(name = "decision_type", nullable = false, length = 40)
    private String decisionType;

    @Column(name = "decision_source", nullable = false, length = 40)
    private String decisionSource;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "decision_reason", columnDefinition = "TEXT")
    private String decisionReason;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "decision_timestamp", nullable = false)
    private LocalDateTime decisionTimestamp;

    @Column(name = "is_final", nullable = false)
    private Boolean finalDecision;

    public TransactionDecision() {
    }

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