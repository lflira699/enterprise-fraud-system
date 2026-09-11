package com.efs.modules.notification.event;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.catalog.dto.NotificationTemplateResponse;
import com.efs.modules.catalog.service.NotificationTemplateServiceInterface;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.integration.service.ProcessedDomainEventRegistry;
import com.efs.modules.notification.entity.Notification;
import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import com.efs.modules.notification.repository.NotificationRecipientDeliveryRepository;
import com.efs.modules.notification.repository.NotificationRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationRequestedEventProcessor {

    private static final String CONSUMER_NAME =
            "Notification Service";

    private static final String INBOUND_EVENT_TYPE =
            "NotificationRequested";

    private static final String OUTBOUND_EVENT_TYPE =
            "NotificationDeliveryCompleted";

    private static final String OUTBOUND_SCHEMA_VERSION =
            "1.0";

    private static final String EVENT_PRODUCER =
            "Notification Service";

    private static final String AGGREGATE_TYPE =
            "Notification";

    private static final String STATUS_PENDING =
            "PENDING";

    private static final String STATUS_PROCESSING =
            "PROCESSING";

    private static final String STATUS_FAILED =
            "FAILED";

    private static final String DELIVERY_STATUS_PENDING =
            "PENDING";

    private static final String ACTIVE_TEMPLATE_STATUS =
            "ACTIVE";

    private static final String NO_AUTHORIZED_RECIPIENTS =
            "NO_AUTHORIZED_RECIPIENTS";

    private static final String DELIVERY_CONFIGURATION_UNAVAILABLE =
            "DELIVERY_CONFIGURATION_UNAVAILABLE";

    private static final String AUDIT_EVENT_TYPE =
            "NOTIFICATION_DELIVERY";

    private static final String AUDIT_ENTITY_TYPE =
            "NOTIFICATION";

    private static final String AUDIT_ACTION =
            "DELIVER";

    private static final String AUDIT_SOURCE_COMPONENT =
            "NOTIFICATION";

    private final ProcessedDomainEventRegistry
            processedDomainEventRegistry;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final NotificationTemplateServiceInterface
            notificationTemplateService;

    private final NotificationRepository
            notificationRepository;

    private final NotificationRecipientDeliveryRepository
            notificationRecipientDeliveryRepository;

    private final AuditEventServiceInterface
            auditEventService;

    private final DomainEventOutboxService
            domainEventOutboxService;

    public NotificationRequestedEventProcessor(
            ProcessedDomainEventRegistry processedDomainEventRegistry,
            UserAccountLookupServiceInterface userAccountLookupService,
            NotificationTemplateServiceInterface notificationTemplateService,
            NotificationRepository notificationRepository,
            NotificationRecipientDeliveryRepository
                    notificationRecipientDeliveryRepository,
            AuditEventServiceInterface auditEventService,
            DomainEventOutboxService domainEventOutboxService) {

        this.processedDomainEventRegistry =
                processedDomainEventRegistry;

        this.userAccountLookupService =
                userAccountLookupService;

        this.notificationTemplateService =
                notificationTemplateService;

        this.notificationRepository =
                notificationRepository;

        this.notificationRecipientDeliveryRepository =
                notificationRecipientDeliveryRepository;

        this.auditEventService =
                auditEventService;

        this.domainEventOutboxService =
                domainEventOutboxService;
    }

    @Transactional
    public NotificationRequestedProcessingResult process(
            NotificationRequestedEventMessage message) {

        if (message == null) {
            throw new IllegalArgumentException(
                    "NotificationRequested event message is required"
            );
        }

        boolean registered =
                processedDomainEventRegistry.register(
                        message.messageId(),
                        CONSUMER_NAME,
                        INBOUND_EVENT_TYPE
                );

        if (!registered) {
            return NotificationRequestedProcessingResult
                    .duplicate();
        }

        Notification notification =
                createPendingNotification(
                        message
                );

        notification =
                notificationRepository
                        .saveAndFlush(
                                notification
                        );

        List<UserAccountReference> authorizedRecipients =
                userAccountLookupService
                        .findAuthorizedUsers(
                                message.organizationId(),
                                message.tenantId(),
                                message.recipientUserIds()
                        );

        if (authorizedRecipients.isEmpty()) {

            rejectNotification(
                    notification,
                    message,
                    NO_AUTHORIZED_RECIPIENTS
            );

            return NotificationRequestedProcessingResult
                    .terminal(
                            notification.getNotificationId()
                    );
        }

        NotificationTemplateResponse template;

        try {

            template =
                    notificationTemplateService
                            .getNotificationTemplateByScope(
                                    message.organizationId(),
                                    message.tenantId(),
                                    message.templateCode(),
                                    message.channel(),
                                    message.languageId()
                            );

        } catch (ResourceNotFoundException exception) {

            rejectNotification(
                    notification,
                    message,
                    DELIVERY_CONFIGURATION_UNAVAILABLE
            );

            return NotificationRequestedProcessingResult
                    .terminal(
                            notification.getNotificationId()
                    );
        }

        if (template.getNotificationTemplateId() == null
                || !ACTIVE_TEMPLATE_STATUS.equals(
                        template.getStatus()
                )) {

            rejectNotification(
                    notification,
                    message,
                    DELIVERY_CONFIGURATION_UNAVAILABLE
            );

            return NotificationRequestedProcessingResult
                    .terminal(
                            notification.getNotificationId()
                    );
        }

        notification.setNotificationTemplateId(
                template.getNotificationTemplateId()
        );

        notificationRepository.saveAndFlush(
                notification
        );

        List<NotificationRecipientDelivery> deliveries =
                new ArrayList<>();

        for (
                UserAccountReference recipient
                : authorizedRecipients
        ) {

            NotificationRecipientDelivery delivery =
                    new NotificationRecipientDelivery();

            delivery.setNotificationId(
                    notification.getNotificationId()
            );

            delivery.setRecipientUserId(
                    recipient.userId()
            );

            delivery.setChannel(
                    message.channel()
            );

            delivery.setDeliveryStatus(
                    DELIVERY_STATUS_PENDING
            );

            delivery.setCreatedAt(
                    LocalDateTime.now()
            );

            deliveries.add(
                    delivery
            );
        }

        notificationRecipientDeliveryRepository
                .saveAll(
                        deliveries
                );

        return NotificationRequestedProcessingResult
                .ready(
                        notification.getNotificationId()
                );    }

    private Notification createPendingNotification(
            NotificationRequestedEventMessage message) {

        Notification notification =
                new Notification();

        LocalDateTime createdAt =
                LocalDateTime.now();

        notification.setOrganizationId(
                message.organizationId()
        );

        notification.setTenantId(
                message.tenantId()
        );

        notification.setNotificationType(
                message.notificationType()
        );

        notification.setSourceComponent(
                message.sourceComponent()
        );

        notification.setSourceEntityType(
                message.sourceEntityType()
        );

        notification.setSourceEntityId(
                message.sourceEntityId()
        );

        notification.setCorrelationId(
                message.correlationId()
        );

        Map<String, Object> payload =
                new LinkedHashMap<>();

        payload.put(
                "templateParameters",
                new LinkedHashMap<>(
                        message.templateParameters()
                )
        );

        notification.setNotificationPayload(
                payload
        );

        notification.setNotificationStatus(
                STATUS_PENDING
        );

        notification.setCreatedAt(
                createdAt
        );

        return notification;
    }

    private void rejectNotification(
            Notification notification,
            NotificationRequestedEventMessage message,
            String reason) {

        notification.setNotificationStatus(
                STATUS_PROCESSING
        );

        notificationRepository.saveAndFlush(
                notification
        );

        LocalDateTime processedAt =
                LocalDateTime.now();

        notification.setNotificationStatus(
                STATUS_FAILED
        );

        notification.setProcessedAt(
                processedAt
        );

        notificationRepository.saveAndFlush(
                notification
        );

        recordRejectedAudit(
                notification,
                message,
                reason
        );

        persistCompletionEvent(
                notification,
                message,
                List.of()
        );
    }

    private void recordRejectedAudit(
            Notification notification,
            NotificationRequestedEventMessage message,
            String reason) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setOrganizationId(
                message.organizationId()
        );

        request.setTenantId(
                message.tenantId()
        );

        request.setEventType(
                AUDIT_EVENT_TYPE
        );

        request.setEntityType(
                AUDIT_ENTITY_TYPE
        );

        request.setEntityId(
                notification.getNotificationId()
        );

        request.setAction(
                AUDIT_ACTION
        );

        request.setSourceComponent(
                AUDIT_SOURCE_COMPONENT
        );

        request.setCorrelationId(
                message.correlationId()
        );

        request.setEventResult(
                "REJECTED"
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "reason",
                reason
        );

        details.put(
                "notificationType",
                message.notificationType()
        );

        details.put(
                "templateCode",
                message.templateCode()
        );

        details.put(
                "channel",
                message.channel()
        );

        details.put(
                "languageId",
                message.languageId().toString()
        );

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
        );
    }

    private void persistCompletionEvent(
            Notification notification,
            NotificationRequestedEventMessage message,
            List<NotificationRecipientDelivery> deliveries) {

        Map<String, Object> payload =
                new LinkedHashMap<>();

        payload.put(
                "notificationId",
                notification
                        .getNotificationId()
                        .toString()
        );

        payload.put(
                "notificationType",
                notification.getNotificationType()
        );

        payload.put(
                "sourceComponent",
                notification.getSourceComponent()
        );

        payload.put(
                "sourceEntityType",
                notification.getSourceEntityType()
        );

        payload.put(
                "sourceEntityId",
                notification
                        .getSourceEntityId()
                        .toString()
        );

        payload.put(
                "notificationStatus",
                notification.getNotificationStatus()
        );

        payload.put(
                "processedAt",
                notification
                        .getProcessedAt()
                        .toString()
        );

        long deliveredCount =
                deliveries.stream()
                        .filter(
                                delivery ->
                                        "DELIVERED".equals(
                                                delivery
                                                        .getDeliveryStatus()
                                        )
                        )
                        .count();

        long failedCount =
                deliveries.stream()
                        .filter(
                                delivery ->
                                        "FAILED".equals(
                                                delivery
                                                        .getDeliveryStatus()
                                        )
                        )
                        .count();

        payload.put(
                "deliveredCount",
                deliveredCount
        );

        payload.put(
                "failedCount",
                failedCount
        );

        List<Map<String, Object>> deliveryPayload =
                new ArrayList<>();

        for (
                NotificationRecipientDelivery delivery
                : deliveries
        ) {

            Map<String, Object> item =
                    new LinkedHashMap<>();

            item.put(
                    "recipientUserId",
                    delivery
                            .getRecipientUserId()
                            .toString()
            );

            item.put(
                    "channel",
                    delivery.getChannel()
            );

            item.put(
                    "deliveryStatus",
                    delivery.getDeliveryStatus()
            );

            item.put(
                    "deliveryReference",
                    delivery.getDeliveryReference()
            );

            item.put(
                    "deliveryResult",
                    delivery.getDeliveryResult()
            );

            item.put(
                    "processedAt",
                    delivery.getProcessedAt() == null
                            ? null
                            : delivery
                                    .getProcessedAt()
                                    .toString()
            );

            deliveryPayload.add(
                    item
            );
        }

        payload.put(
                "deliveries",
                deliveryPayload
        );

        DomainEventEnvelope envelope =
                new DomainEventEnvelope();

        envelope.setEventType(
                OUTBOUND_EVENT_TYPE
        );

        envelope.setSchemaVersion(
                OUTBOUND_SCHEMA_VERSION
        );

        envelope.setOccurredAt(
                notification.getProcessedAt()
        );

        envelope.setProducer(
                EVENT_PRODUCER
        );

        envelope.setCorrelationId(
                message.correlationId()
        );

        envelope.setCausationId(
                message.messageId()
        );

        envelope.setTenantId(
                message.tenantId()
        );

        envelope.setPayload(
                payload
        );

        envelope.setMetadata(
                Map.of()
        );

        domainEventOutboxService.persist(
                AGGREGATE_TYPE,
                notification.getNotificationId(),
                envelope
        );
    }
}