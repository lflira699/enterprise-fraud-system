package com.efs.modules.casemanagement.mapper;

import com.efs.modules.casemanagement.dto.CaseNotificationRequest;
import com.efs.modules.casemanagement.dto.CaseNotificationResponse;
import com.efs.modules.casemanagement.entity.CaseNotification;
import org.springframework.stereotype.Component;

@Component
public class CaseNotificationMapper {

    public CaseNotification toEntity(
            CaseNotificationRequest request) {

        CaseNotification notification =
                new CaseNotification();

        notification.setNotificationType(
                request.getNotificationType()
        );

        notification.setRecipientUserId(
                request.getRecipientUserId()
        );

        notification.setNotificationStatus(
                request.getNotificationStatus()
        );

        notification.setNotificationReference(
                request.getNotificationReference()
        );

        notification.setDeliveryResult(
                request.getDeliveryResult()
        );

        notification.setProcessedAt(
                request.getProcessedAt()
        );

        return notification;
    }

    public CaseNotificationResponse toResponse(
            CaseNotification notification) {

        CaseNotificationResponse response =
                new CaseNotificationResponse();

        response.setCaseNotificationId(
                notification.getCaseNotificationId()
        );

        response.setCaseId(
                notification.getCaseId()
        );

        response.setNotificationType(
                notification.getNotificationType()
        );

        response.setRecipientUserId(
                notification.getRecipientUserId()
        );

        response.setNotificationStatus(
                notification.getNotificationStatus()
        );

        response.setNotificationReference(
                notification.getNotificationReference()
        );

        response.setDeliveryResult(
                notification.getDeliveryResult()
        );

        response.setCreatedAt(
                notification.getCreatedAt()
        );

        response.setProcessedAt(
                notification.getProcessedAt()
        );

        return response;
    }
}