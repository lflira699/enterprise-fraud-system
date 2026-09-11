package com.efs.modules.notification.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.integration.event.DomainEventEnvelope;
import com.efs.modules.integration.service.DomainEventOutboxService;
import com.efs.modules.notification.entity.Notification;
import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import com.efs.modules.notification.event.NotificationRequestedEventMessage;
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
import java.util.Objects;
import java.util.UUID;

@Service
public class NotificationDeliveryTerminalizationService {

    private static final String STATUS_PENDING =
            "PENDING";

    private static final String STATUS_PROCESSING =
            "PROCESSING";

    private static final String STATUS_FAILED =
            "FAILED";

    private static final String DELIVERY_STATUS_PENDING =
            "PENDING";

    private static final String DELIVERY_STATUS_FAILED =
            "FAILED";

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

    private static final String AUDIT_RESULT_REJECTED =
            "REJECTED";

    private static final String OUTBOUND_EVENT_TYPE =
            "NotificationDeliveryCompleted";

    private static final String OUTBOUND_SCHEMA_VERSION =
            "1.0";

    private static final String EVENT_PRODUCER =
            "Notification Service";

    private static final String AGGREGATE_TYPE =
            "Notification";

    private final NotificationRepository
            notificationRepository;

    private final NotificationRecipientDeliveryRepository
            notificationRecipientDeliveryRepository;

    private final AuditEventServiceInterface
            auditEventService;

    private final DomainEventOutboxService
            domainEventOutboxService;

    public NotificationDeliveryTerminalizationService(
            NotificationRepository notificationRepository,
            NotificationRecipientDeliveryRepository
                    notificationRecipientDeliveryRepository,
            AuditEventServiceInterface auditEventService,
            DomainEventOutboxService domainEventOutboxService) {

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
    public void rejectPreflight(
            UUID notificationId,
            NotificationRequestedEventMessage message,
            String reason) {

        requireInputs(
                notificationId,
                message,
                reason
        );

        Notification notification =
                notificationRepository
                        .findByNotificationIdForUpdate(
                                notificationId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Notification not found: "
                                                        + notificationId
                                        )
                        );

        requirePendingNotification(
                notification
        );

        requireMessageContext(
                notification,
                message
        );

        List<NotificationRecipientDelivery> deliveries =
                notificationRecipientDeliveryRepository
                        .findByNotificationIdOrderByCreatedAtAsc(
                                notificationId
                        );

        requirePendingDeliveries(
                notification,
                deliveries
        );

        notification.setNotificationStatus(
                STATUS_PROCESSING
        );

        notificationRepository.saveAndFlush(
                notification
        );

        LocalDateTime processedAt =
                LocalDateTime.now();

        for (
                NotificationRecipientDelivery delivery
                : deliveries
        ) {

            delivery.setDeliveryStatus(
                    DELIVERY_STATUS_FAILED
            );

            delivery.setDeliveryReference(
                    null
            );

            delivery.setDeliveryResult(
                    reason
            );

            delivery.setProcessedAt(
                    processedAt
            );
        }

        notificationRecipientDeliveryRepository
                .saveAll(
                        deliveries
                );

        notification.setNotificationStatus(
                STATUS_FAILED
        );

        notification.setProcessedAt(
                processedAt
        );

        notificationRepository.save(
                notification
        );

        recordRejectedAudit(
                notification,
                reason
        );

        persistCompletionEvent(
                notification,
                message,
                deliveries
        );
    }

    private void requireInputs(
            UUID notificationId,
            NotificationRequestedEventMessage message,
            String reason) {

        if (notificationId == null) {
            throw new IllegalArgumentException(
                    "Notification id is required"
            );
        }

        if (message == null) {
            throw new IllegalArgumentException(
                    "NotificationRequested event message is required"
            );
        }

        if (!NO_AUTHORIZED_RECIPIENTS.equals(reason)
                && !DELIVERY_CONFIGURATION_UNAVAILABLE.equals(
                        reason
                )) {

            throw new IllegalArgumentException(
                    "Unsupported controlled rejection reason: "
                            + reason
            );
        }
    }

    private void requirePendingNotification(
            Notification notification) {

        if (!STATUS_PENDING.equals(
                notification.getNotificationStatus()
        )) {

            throw new IllegalStateException(
                    "Notification must be PENDING "
                            + "for preflight rejection"
            );
        }
    }

    private void requireMessageContext(
            Notification notification,
            NotificationRequestedEventMessage message) {

        boolean valid =
                Objects.equals(
                        notification.getOrganizationId(),
                        message.organizationId()
                )
                && Objects.equals(
                        notification.getTenantId(),
                        message.tenantId()
                )
                && Objects.equals(
                        notification.getNotificationType(),
                        message.notificationType()
                )
                && Objects.equals(
                        notification.getSourceComponent(),
                        message.sourceComponent()
                )
                && Objects.equals(
                        notification.getSourceEntityType(),
                        message.sourceEntityType()
                )
                && Objects.equals(
                        notification.getSourceEntityId(),
                        message.sourceEntityId()
                )
                && Objects.equals(
                        notification.getCorrelationId(),
                        message.correlationId()
                );

        if (!valid) {
            throw new IllegalStateException(
                    "NotificationRequested context "
                            + "does not match Notification"
            );
        }
    }

    private void requirePendingDeliveries(
            Notification notification,
            List<NotificationRecipientDelivery> deliveries) {

        for (
                NotificationRecipientDelivery delivery
                : deliveries
        ) {

            if (delivery == null) {
                throw new IllegalStateException(
                        "Notification delivery must not be null"
                );
            }

            if (!Objects.equals(
                    notification.getNotificationId(),
                    delivery.getNotificationId()
            )) {

                throw new IllegalStateException(
                        "Notification delivery belongs "
                                + "to a different Notification"
                );
            }

            if (!DELIVERY_STATUS_PENDING.equals(
                    delivery.getDeliveryStatus()
            )) {

                throw new IllegalStateException(
                        "Notification delivery must be PENDING "
                                + "for preflight rejection"
                );
            }
        }
    }

    private void recordRejectedAudit(
            Notification notification,
            String reason) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setTenantId(
                notification.getTenantId()
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
                notification.getCorrelationId()
        );

        request.setEventResult(
                AUDIT_RESULT_REJECTED
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "reason",
                reason
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
                                        DELIVERY_STATUS_FAILED.equals(
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