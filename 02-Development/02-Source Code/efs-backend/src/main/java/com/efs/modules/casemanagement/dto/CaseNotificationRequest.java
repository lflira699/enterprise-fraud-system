package com.efs.modules.casemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class CaseNotificationRequest {

    @NotBlank
    @Size(max = 50)
    private String notificationType;

    private UUID recipientUserId;

    @NotBlank
    @Size(max = 30)
    private String notificationStatus;

    @Size(max = 120)
    private String notificationReference;

    @Size(max = 50)
    private String deliveryResult;

    private LocalDateTime processedAt;

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

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(
            LocalDateTime processedAt) {

        this.processedAt =
                processedAt;
    }
}