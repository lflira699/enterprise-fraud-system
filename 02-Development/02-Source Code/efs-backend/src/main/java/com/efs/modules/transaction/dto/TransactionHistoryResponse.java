package com.efs.modules.transaction.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class TransactionHistoryResponse {

    private UUID historyId;
    private UUID transactionId;
    private Integer versionNumber;
    private Map<String, Object> snapshotJson;
    private String changeReason;
    private UUID changedBy;
    private LocalDateTime changedAt;

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