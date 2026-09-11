package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.NotificationTemplateRequest;
import com.efs.modules.catalog.dto.NotificationTemplateResponse;
import com.efs.modules.catalog.dto.NotificationTemplateUpdateRequest;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface NotificationTemplateServiceInterface {

    NotificationTemplateResponse createNotificationTemplate(
            NotificationTemplateRequest request
    );

    NotificationTemplateResponse updateNotificationTemplate(
            UUID notificationTemplateId,
            NotificationTemplateUpdateRequest request,
            SecurityContext securityContext
    );

    NotificationTemplateResponse getNotificationTemplateById(
            UUID notificationTemplateId
    );

    NotificationTemplateResponse getNotificationTemplateByScope(
            UUID organizationId,
            UUID tenantId,
            String templateCode,
            String channel,
            UUID languageId
    );

    List<NotificationTemplateResponse> getNotificationTemplatesByScope(
            UUID organizationId,
            UUID tenantId
    );

    List<NotificationTemplateResponse> getNotificationTemplatesByScopeAndStatus(
            UUID organizationId,
            UUID tenantId,
            String status
    );

    List<NotificationTemplateResponse> getNotificationTemplatesByCode(
            String templateCode
    );
}