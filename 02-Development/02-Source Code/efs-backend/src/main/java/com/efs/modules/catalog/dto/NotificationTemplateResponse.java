package com.efs.modules.catalog.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationTemplateResponse {

    private UUID notificationTemplateId;
    private UUID organizationId;
    private UUID tenantId;
    private UUID languageId;
    private String templateCode;
    private String templateName;
    private String channel;
    private String subjectTemplate;
    private String bodyTemplate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NotificationTemplateResponse() {
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