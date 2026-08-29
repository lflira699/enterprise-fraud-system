package com.efs.modules.catalog.mapper;

import com.efs.modules.catalog.dto.NotificationTemplateRequest;
import com.efs.modules.catalog.dto.NotificationTemplateResponse;
import com.efs.modules.catalog.entity.NotificationTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationTemplateMapper {

    public NotificationTemplate toEntity(
            NotificationTemplateRequest request) {

        NotificationTemplate notificationTemplate =
                new NotificationTemplate();

        notificationTemplate.setOrganizationId(
                request.getOrganizationId()
        );

        notificationTemplate.setTenantId(
                request.getTenantId()
        );

        notificationTemplate.setLanguageId(
                request.getLanguageId()
        );

        notificationTemplate.setTemplateCode(
                request.getTemplateCode()
        );

        notificationTemplate.setTemplateName(
                request.getTemplateName()
        );

        notificationTemplate.setChannel(
                request.getChannel()
        );

        notificationTemplate.setSubjectTemplate(
                request.getSubjectTemplate()
        );

        notificationTemplate.setBodyTemplate(
                request.getBodyTemplate()
        );

        notificationTemplate.setStatus(
                request.getStatus()
        );

        return notificationTemplate;
    }

    public NotificationTemplateResponse toResponse(
            NotificationTemplate notificationTemplate) {

        NotificationTemplateResponse response =
                new NotificationTemplateResponse();

        response.setNotificationTemplateId(
                notificationTemplate.getNotificationTemplateId()
        );

        response.setOrganizationId(
                notificationTemplate.getOrganizationId()
        );

        response.setTenantId(
                notificationTemplate.getTenantId()
        );

        response.setLanguageId(
                notificationTemplate.getLanguageId()
        );

        response.setTemplateCode(
                notificationTemplate.getTemplateCode()
        );

        response.setTemplateName(
                notificationTemplate.getTemplateName()
        );

        response.setChannel(
                notificationTemplate.getChannel()
        );

        response.setSubjectTemplate(
                notificationTemplate.getSubjectTemplate()
        );

        response.setBodyTemplate(
                notificationTemplate.getBodyTemplate()
        );

        response.setStatus(
                notificationTemplate.getStatus()
        );

        response.setCreatedAt(
                notificationTemplate.getCreatedAt()
        );

        response.setUpdatedAt(
                notificationTemplate.getUpdatedAt()
        );

        return response;
    }
}