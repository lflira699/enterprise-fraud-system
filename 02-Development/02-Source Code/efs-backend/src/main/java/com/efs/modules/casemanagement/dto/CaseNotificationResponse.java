package com.efs.modules.casemanagement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CaseNotificationResponse {

    private UUID caseNotificationId;
    private UUID caseId;
    private String notificationType;
    private UUID recipientUserId;
    private String notificationStatus;
    private String notificationReference;
    private String deliveryResult;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public UUID getCaseNotificationId() {
        return caseNotificationId;
    }

    public void setCaseNotificationId(
            UUID caseNotificationId) {

        this.caseNotificationId =
                caseNotificationId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(
            UUID caseId) {

        this.caseId =
                caseId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(
            String notificationType) {

        this.notificationType =
                notificationType;
    }

    public UUID getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(
            UUID recipientUserId) {

        this.recipientUserId =
                recipientUserId;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(
            String notificationStatus) {

        this.notificationStatus =
                notificationStatus;
    }

    public String getNotificationReference() {
        return notificationReference;
    }

    public void setNotificationReference(
            String notificationReference) {

        this.notificationReference =
                notificationReference;
    }

    public String getDeliveryResult() {
        return deliveryResult;
    }

    public void setDeliveryResult(
            String deliveryResult) {

        this.deliveryResult =
                deliveryResult;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt =
                createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(
            LocalDateTime processedAt) {

        this.processedAt =
                processedAt;
    }
}