package com.efs.modules.catalog.entity;

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
@Table(name = "notification_template", schema = "catalog")
public class NotificationTemplate {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(
            name = "notification_template_id",
            nullable = false
    )
    private UUID notificationTemplateId;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "language_id")
    private UUID languageId;

    @Column(
            name = "template_code",
            nullable = false,
            length = 60
    )
    private String templateCode;

    @Column(
            name = "template_name",
            nullable = false,
            length = 150
    )
    private String templateName;

    @Column(
            name = "channel",
            nullable = false,
            length = 30
    )
    private String channel;

    @Column(
            name = "subject_template",
            length = 250
    )
    private String subjectTemplate;

    @Column(
            name = "body_template",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String bodyTemplate;

    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public NotificationTemplate() {
    }

    @PrePersist
    public void prePersist() {

        LocalDateTime now =
                LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    public UUID getNotificationTemplateId() {
        return notificationTemplateId;
    }

    public void setNotificationTemplateId(
            UUID notificationTemplateId) {

        this.notificationTemplateId =
                notificationTemplateId;
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

    public UUID getLanguageId() {
        return languageId;
    }

    public void setLanguageId(
            UUID languageId) {

        this.languageId =
                languageId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(
            String templateCode) {

        this.templateCode =
                templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(
            String templateName) {

        this.templateName =
                templateName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(
            String channel) {

        this.channel =
                channel;
    }

    public String getSubjectTemplate() {
        return subjectTemplate;
    }

    public void setSubjectTemplate(
            String subjectTemplate) {

        this.subjectTemplate =
                subjectTemplate;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(
            String bodyTemplate) {

        this.bodyTemplate =
                bodyTemplate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {

        this.status =
                status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt =
                createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt =
                updatedAt;
    }
}