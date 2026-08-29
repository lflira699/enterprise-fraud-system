package com.efs.modules.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class NotificationTemplateRequest {

    private UUID organizationId;

    private UUID tenantId;

    private UUID languageId;

    @NotBlank
    @Size(max = 60)
    private String templateCode;

    @NotBlank
    @Size(max = 150)
    private String templateName;

    @NotBlank
    @Size(max = 30)
    private String channel;

    @Size(max = 250)
    private String subjectTemplate;

    @NotBlank
    private String bodyTemplate;

    @NotBlank
    @Size(max = 20)
    private String status;

    public NotificationTemplateRequest() {
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
}