package com.efs.modules.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "notification",
        schema = "notification"
)
public class Notification {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(
            name = "notification_id",
            nullable = false
    )
    private UUID notificationId;

    @Column(
            name = "organization_id",
            nullable = false
    )
    private UUID organizationId;

    @Column(
            name = "tenant_id",
            nullable = false
    )
    private UUID tenantId;

    @Column(
            name = "notification_type",
            nullable = false,
            length = 50
    )
    private String notificationType;

    @Column(
            name = "notification_template_id"
    )
    private UUID notificationTemplateId;

    @Column(
            name = "source_component",
            nullable = false,
            length = 100
    )
    private String sourceComponent;

    @Column(
            name = "source_entity_type",
            nullable = false,
            length = 60
    )
    private String sourceEntityType;

    @Column(
            name = "source_entity_id",
            nullable = false
    )
    private UUID sourceEntityId;

    @Column(
            name = "correlation_id",
            nullable = false
    )
    private UUID correlationId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "notification_payload",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, Object> notificationPayload;

    @Column(
            name = "notification_status",
            nullable = false,
            length = 30
    )
    private String notificationStatus;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "processed_at"
    )
    private LocalDateTime processedAt;

    public Notification() {
    }

    @PrePersist
    public void prePersist() {

        if (notificationStatus == null) {
            notificationStatus = "PENDING";
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(
            UUID notificationId) {

        this.notificationId =
                notificationId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(
            UUID organizationId) {

        this.organizationId =
                organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            UUID tenantId) {

        this.tenantId =
                tenantId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(
            String notificationType) {

        this.notificationType =
                notificationType;
    }

    public UUID getNotificationTemplateId() {
        return notificationTemplateId;
    }

    public void setNotificationTemplateId(
            UUID notificationTemplateId) {

        this.notificationTemplateId =
                notificationTemplateId;
    }

    public String getSourceComponent() {
        return sourceComponent;
    }

    public void setSourceComponent(
            String sourceComponent) {

        this.sourceComponent =
                sourceComponent;
    }

    public String getSourceEntityType() {
        return sourceEntityType;
    }

    public void setSourceEntityType(
            String sourceEntityType) {

        this.sourceEntityType =
                sourceEntityType;
    }

    public UUID getSourceEntityId() {
        return sourceEntityId;
    }

    public void setSourceEntityId(
            UUID sourceEntityId) {

        this.sourceEntityId =
                sourceEntityId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(
            UUID correlationId) {

        this.correlationId =
                correlationId;
    }

    public Map<String, Object> getNotificationPayload() {
        return notificationPayload;
    }

    public void setNotificationPayload(
            Map<String, Object> notificationPayload) {

        this.notificationPayload =
                notificationPayload;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(
            String notificationStatus) {

        this.notificationStatus =
                notificationStatus;
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
