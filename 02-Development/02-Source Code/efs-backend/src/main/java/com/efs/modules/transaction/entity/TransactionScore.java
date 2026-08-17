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
@Table(name = "transaction_score", schema = "transaction")
public class TransactionScore {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "score_id", nullable = false)
    private UUID scoreId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "score_type", nullable = false, length = 40)
    private String scoreType;

    @Column(name = "score_value", nullable = false, precision = 8, scale = 2)
    private BigDecimal scoreValue;

    @Column(name = "score_weight", precision = 5, scale = 2)
    private BigDecimal scoreWeight;

    @Column(name = "scoring_model", length = 80)
    private String scoringModel;

    @Column(name = "model_version", length = 20)
    private String modelVersion;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    public TransactionScore() {
    }

    public UUID getScoreId() {
        return scoreId;
    }

    public void setScoreId(UUID scoreId) {
        this.scoreId = scoreId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    public BigDecimal getScoreWeight() {
        return scoreWeight;
    }

    public void setScoreWeight(BigDecimal scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    public String getScoringModel() {
        return scoringModel;
    }

    public void setScoringModel(String scoringModel) {
        this.scoringModel = scoringModel;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}