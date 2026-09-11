package com.efs.modules.catalog.dto;

import jakarta.validation.constraints.Size;

public class NotificationTemplateUpdateRequest {

    @Size(max = 150)
    private String templateName;

    @Size(max = 250)
    private String subjectTemplate;

    private String bodyTemplate;

    @Size(max = 20)
    private String status;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(
            String templateName) {

        this.templateName =
                templateName;
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