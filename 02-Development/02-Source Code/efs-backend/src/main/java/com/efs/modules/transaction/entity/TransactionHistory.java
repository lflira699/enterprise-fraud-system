package com.efs.modules.transaction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "transaction_history", schema = "transaction")
public class TransactionHistory {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "history_id", nullable = false)
    private UUID historyId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "snapshot_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> snapshotJson;

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    @Column(name = "changed_by")
    private UUID changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public TransactionHistory() {
    }

    public UUID getHistoryId() {
        return historyId;
    }

    public void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Map<String, Object> getSnapshotJson() {
        return snapshotJson;
    }

    public void setSnapshotJson(Map<String, Object> snapshotJson) {
        this.snapshotJson = snapshotJson;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(UUID changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}