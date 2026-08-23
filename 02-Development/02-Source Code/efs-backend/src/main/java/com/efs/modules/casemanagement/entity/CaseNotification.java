package com.efs.modules.casemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "case_notification",
        schema = "case_management"
)
public class CaseNotification {

    @Id
    @GeneratedValue
    @Column(
            name = "case_notification_id",
            nullable = false
    )
    private UUID caseNotificationId;

    @Column(
            name = "case_id",
            nullable = false
    )
    private UUID caseId;

    @Column(
            name = "notification_type",
            nullable = false,
            length = 50
    )
    private String notificationType;

    @Column(
            name = "recipient_user_id"
    )
    private UUID recipientUserId;

    @Column(
            name = "notification_status",
            nullable = false,
            length = 30
    )
    private String notificationStatus;

    @Column(
            name = "notification_reference",
            length = 120
    )
    private String notificationReference;

    @Column(
            name = "delivery_result",
            length = 50
    )
    private String deliveryResult;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "processed_at"
    )
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