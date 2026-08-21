package com.efs.modules.alert.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlertHistoryResponse {

    private UUID alertHistoryId;
    private UUID alertId;
    private String actionType;
    private String previousStatus;
    private String newStatus;
    private UUID changedBy;
    private String changeReason;
    private LocalDateTime changedAt;

    public UUID getAlertHistoryId() {
        return alertHistoryId;
    }

    public void setAlertHistoryId(UUID alertHistoryId) {
        this.alertHistoryId = alertHistoryId;
    }

    public UUID getAlertId() {
        return alertId;
    }

    public void setAlertId(UUID alertId) {
        this.alertId = alertId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(UUID changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}