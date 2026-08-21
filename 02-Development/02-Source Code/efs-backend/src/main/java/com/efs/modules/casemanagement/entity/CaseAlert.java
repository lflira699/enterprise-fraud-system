package com.efs.modules.casemanagement.entity;

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
@Table(name = "case_alert", schema = "case_management")
public class CaseAlert {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "alert_id", nullable = false)
    private UUID alertId;

    @Column(name = "case_id", nullable = false)
    private UUID caseId;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "alert_type", nullable = false, length = 40)
    private String alertType;

    @Column(name = "alert_source", nullable = false, length = 50)
    private String alertSource;

    @Column(name = "risk_score", precision = 8, scale = 2)
    private BigDecimal riskScore;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "source_alert_id")
    private UUID sourceAlertId;

    public CaseAlert() {
    }

    public UUID getAlertId() {
        return alertId;
    }

    public void setAlertId(UUID alertId) {
        this.alertId = alertId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertSource() {
        return alertSource;
    }

    public void setAlertSource(String alertSource) {
        this.alertSource = alertSource;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public UUID getSourceAlertId() {
        return sourceAlertId;
    }

    public void setSourceAlertId(UUID sourceAlertId) {
        this.sourceAlertId = sourceAlertId;
    }
}