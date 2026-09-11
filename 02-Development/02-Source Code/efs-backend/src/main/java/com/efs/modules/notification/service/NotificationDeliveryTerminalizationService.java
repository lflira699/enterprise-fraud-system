package com.efs.modules.notification.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;
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

    private static final String AUDIT_RESULT_SUCCESS =
            "SUCCESS";

    private static final String AUDIT_RESULT_FAILURE =
            "FAILURE";

    private static final String NOTIFICATION_DELIVERY_FAILED =
            "NOTIFICATION_DELIVERY_FAILED";

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

    @Transactional
    public void completeConfirmedResults(
            UUID notificationId,
            NotificationRequestedEventMessage message,
            Map<UUID, ExternalNotificationDeliveryResult>
                    confirmedResults) {

        requireCompletionInputs(
                notificationId,
                message,
                confirmedResults
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

        requireProcessingNotification(
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

        requireConfirmedResults(
                notification,
                deliveries,
                confirmedResults
        );

        LocalDateTime processedAt =
                LocalDateTime.now();

        long deliveredCount =
                0L;

        for (
                NotificationRecipientDelivery delivery
                : deliveries
        ) {

            ExternalNotificationDeliveryResult result =
                    confirmedResults.get(
                            delivery.getNotificationDeliveryId()
                    );

            if (result.isDelivered()) {

                delivery.setDeliveryStatus(
                        "DELIVERED"
                );

                deliveredCount++;

            } else {

                delivery.setDeliveryStatus(
                        DELIVERY_STATUS_FAILED
                );
            }

            delivery.setDeliveryReference(
                    result.getDeliveryReference()
            );

            delivery.setDeliveryResult(
                    result.getDeliveryResult()
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
                deriveTerminalStatus(
                        deliveredCount,
                        deliveries.size()
                )
        );

        notification.setProcessedAt(
                processedAt
        );

        notificationRepository.save(
                notification
        );

        recordSuccessfulAudit(
                notification
        );

        persistCompletionEvent(
                notification,
                message,
                deliveries
        );
    }

    @Transactional
    public void completeUnexpectedFailure(
            UUID notificationId,
            NotificationRequestedEventMessage message,
            Map<UUID, ExternalNotificationDeliveryResult>
                    confirmedResults,
            UUID failedDeliveryId) {

        requireUnexpectedFailureInputs(
                notificationId,
                message,
                confirmedResults,
                failedDeliveryId
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

        requireProcessingNotification(
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

        requireUnexpectedFailureResults(
                notification,
                deliveries,
                confirmedResults,
                failedDeliveryId
        );

        LocalDateTime processedAt =
                LocalDateTime.now();

        long deliveredCount =
                0L;

        for (
                NotificationRecipientDelivery delivery
                : deliveries
        ) {

            ExternalNotificationDeliveryResult confirmedResult =
                    confirmedResults.get(
                            delivery.getNotificationDeliveryId()
                    );

            if (confirmedResult != null) {

                if (confirmedResult.isDelivered()) {

                    delivery.setDeliveryStatus(
                            "DELIVERED"
                    );

                    deliveredCount++;

                } else {

                    delivery.setDeliveryStatus(
                            DELIVERY_STATUS_FAILED
                    );
                }

                delivery.setDeliveryReference(
                        confirmedResult.getDeliveryReference()
                );

                delivery.setDeliveryResult(
                        confirmedResult.getDeliveryResult()
                );

            } else {

                delivery.setDeliveryStatus(
                        DELIVERY_STATUS_FAILED
                );

                delivery.setDeliveryReference(
                        null
                );

                delivery.setDeliveryResult(
                        NOTIFICATION_DELIVERY_FAILED
                );
            }

            delivery.setProcessedAt(
                    processedAt
            );
        }

        notificationRecipientDeliveryRepository
                .saveAll(
                        deliveries
                );

        notification.setNotificationStatus(
                deriveTerminalStatus(
                        deliveredCount,
                        deliveries.size()
                )
        );

        notification.setProcessedAt(
                processedAt
        );

        notificationRepository.save(
                notification
        );

        recordFailureAudit(
                notification
        );

        persistCompletionEvent(
                notification,
                message,
                deliveries
        );
    }

    private void requireUnexpectedFailureInputs(
            UUID notificationId,
            NotificationRequestedEventMessage message,
            Map<UUID, ExternalNotificationDeliveryResult>
                    confirmedResults,
            UUID failedDeliveryId) {

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

        if (confirmedResults == null) {
            throw new IllegalArgumentException(
                    "Confirmed delivery results are required"
            );
        }

        if (failedDeliveryId == null) {
            throw new IllegalArgumentException(
                    "Failed delivery id is required"
            );
        }

        if (confirmedResults.containsKey(
                failedDeliveryId
        )) {

            throw new IllegalArgumentException(
                    "Failed delivery must not already have "
                            + "a confirmed result"
            );
        }
    }

    private void requireUnexpectedFailureResults(
            Notification notification,
            List<NotificationRecipientDelivery> deliveries,
            Map<UUID, ExternalNotificationDeliveryResult>
                    confirmedResults,
            UUID failedDeliveryId) {

        if (deliveries.isEmpty()) {
            throw new IllegalStateException(
                    "Notification deliveries are required"
            );
        }

        boolean failedDeliveryFound =
                false;

        Map<UUID, NotificationRecipientDelivery>
                deliveriesById =
                new LinkedHashMap<>();

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
                                + "before unexpected failure terminalization"
                );
            }

            UUID deliveryId =
                    delivery.getNotificationDeliveryId();

            if (deliveryId == null) {
                throw new IllegalStateException(
                        "Notification delivery id is required"
                );
            }

            deliveriesById.put(
                    deliveryId,
                    delivery
            );

            if (deliveryId.equals(
                    failedDeliveryId
            )) {

                failedDeliveryFound =
                        true;
            }
        }

        if (!failedDeliveryFound) {
            throw new IllegalStateException(
                    "Failed delivery does not belong "
                            + "to Notification"
            );
        }

        for (
                Map.Entry<
                        UUID,
                        ExternalNotificationDeliveryResult
                        > entry
                : confirmedResults.entrySet()
        ) {

            if (entry.getKey() == null
                    || entry.getValue() == null
                    || !deliveriesById.containsKey(
                            entry.getKey()
                    )) {

                throw new IllegalStateException(
                        "Confirmed result references "
                                + "an invalid Notification delivery"
                );
            }
        }
    }
    private void requireCompletionInputs(
            UUID notificationId,
            NotificationRequestedEventMessage message,
            Map<UUID, ExternalNotificationDeliveryResult>
                    confirmedResults) {

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

        if (confirmedResults == null) {
            throw new IllegalArgumentException(
                    "Confirmed delivery results are required"
            );
        }
    }

    private void requireProcessingNotification(
            Notification notification) {

        if (!STATUS_PROCESSING.equals(
                notification.getNotificationStatus()
        )) {

            throw new IllegalStateException(
                    "Notification must be PROCESSING "
                            + "for confirmed result terminalization"
            );
        }
    }

    private void requireConfirmedResults(
            Notification notification,
            List<NotificationRecipientDelivery> deliveries,
            Map<UUID, ExternalNotificationDeliveryResult>
                    confirmedResults) {

        if (deliveries.isEmpty()) {
            throw new IllegalStateException(
                    "Notification deliveries are required"
            );
        }

        if (confirmedResults.size()
                != deliveries.size()) {

            throw new IllegalStateException(
                    "Confirmed delivery results must match "
                            + "all Notification deliveries"
            );
        }

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
                                + "before confirmed result terminalization"
                );
            }

            UUID deliveryId =
                    delivery.getNotificationDeliveryId();

            if (deliveryId == null
                    || !confirmedResults.containsKey(
                            deliveryId
                    )
                    || confirmedResults.get(
                            deliveryId
                    ) == null) {

                throw new IllegalStateException(
                        "Confirmed result is required "
                                + "for every Notification delivery"
                );
            }
        }
    }

    private String deriveTerminalStatus(
            long deliveredCount,
            int totalCount) {

        if (deliveredCount == totalCount) {
            return "DELIVERED";
        }

        if (deliveredCount == 0L) {
            return STATUS_FAILED;
        }

        return "PARTIALLY_DELIVERED";
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

        recordAudit(
                notification,
                AUDIT_RESULT_REJECTED,
                Map.of(
                        "reason",
                        reason
                )
        );
    }

    private void recordSuccessfulAudit(
            Notification notification) {

        recordAudit(
                notification,
                AUDIT_RESULT_SUCCESS,
                Map.of()
        );
    }

    private void recordFailureAudit(
            Notification notification) {

        recordAudit(
                notification,
                AUDIT_RESULT_FAILURE,
                Map.of(
                        "reason",
                        NOTIFICATION_DELIVERY_FAILED
                )
        );
    }

    private void recordAudit(
            Notification notification,
            String eventResult,
            Map<String, Object> eventDetails) {

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
                eventResult
        );

        request.setEventDetails(
                eventDetails
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