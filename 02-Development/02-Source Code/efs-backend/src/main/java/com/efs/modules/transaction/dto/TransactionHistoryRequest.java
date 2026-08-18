package com.efs.modules.transaction.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public class TransactionHistoryRequest {

    @NotNull
    private Integer versionNumber;

    @NotNull
    private Map<String, Object> snapshotJson;

    private String changeReason;

    private UUID changedBy;

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
}