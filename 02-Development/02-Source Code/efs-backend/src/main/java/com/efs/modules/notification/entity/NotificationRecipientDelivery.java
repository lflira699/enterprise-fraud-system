package com.efs.modules.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "notification_recipient_delivery",
        schema = "notification"
)
public class NotificationRecipientDelivery {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(
            name = "notification_delivery_id",
            nullable = false
    )
    private UUID notificationDeliveryId;

    @Column(
            name = "notification_id",
            nullable = false
    )
    private UUID notificationId;

    @Column(
            name = "recipient_user_id",
            nullable = false
    )
    private UUID recipientUserId;

    @Column(
            name = "channel",
            nullable = false,
            length = 30
    )
    private String channel;

    @Column(
            name = "delivery_status",
            nullable = false,
            length = 30
    )
    private String deliveryStatus;

    @Column(
            name = "delivery_reference",
            length = 120
    )
    private String deliveryReference;

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

    public NotificationRecipientDelivery() {
    }

    @PrePersist
    public void prePersist() {

        if (deliveryStatus == null) {
            deliveryStatus = "PENDING";
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public UUID getNotificationDeliveryId() {
        return notificationDeliveryId;
    }

    public void setNotificationDeliveryId(
            UUID notificationDeliveryId) {

        this.notificationDeliveryId =
                notificationDeliveryId;
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(
            UUID notificationId) {

        this.notificationId =
                notificationId;
    }

    public UUID getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(
            UUID recipientUserId) {

        this.recipientUserId =
                recipientUserId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(
            String channel) {

        this.channel =
                channel;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(
            String deliveryStatus) {

        this.deliveryStatus =
                deliveryStatus;
    }

    public String getDeliveryReference() {
        return deliveryReference;
    }

    public void setDeliveryReference(
            String deliveryReference) {

        this.deliveryReference =
                deliveryReference;
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
