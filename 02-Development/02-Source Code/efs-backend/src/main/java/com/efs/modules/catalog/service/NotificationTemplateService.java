package com.efs.modules.catalog.service;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.service.AuditEntityChangeServiceInterface;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.catalog.dto.NotificationTemplateRequest;
import com.efs.modules.catalog.dto.NotificationTemplateResponse;
import com.efs.modules.catalog.dto.NotificationTemplateUpdateRequest;
import com.efs.modules.catalog.entity.NotificationTemplate;
import com.efs.modules.catalog.mapper.NotificationTemplateMapper;
import com.efs.modules.catalog.repository.NotificationTemplateRepository;
import com.efs.shared.exception.RequestValidationException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
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
@Transactional
public class NotificationTemplateService
        implements NotificationTemplateServiceInterface {

    private static final String NOTIFICATION_MANAGE_PERMISSION =
            "notification.manage";

    private static final String NOTIFICATION_MANAGEMENT_EVENT_TYPE =
            "NOTIFICATION_MANAGEMENT";

    private static final String NOTIFICATION_MANAGEMENT_ENTITY_TYPE =
            "NOTIFICATION_TEMPLATE";

    private static final String NOTIFICATION_MANAGEMENT_ACTION =
            "UPDATE";

    private static final String NOTIFICATION_MANAGEMENT_SOURCE_COMPONENT =
            "CATALOG";

    private final NotificationTemplateRepository
            notificationTemplateRepository;

    private final NotificationTemplateMapper
            notificationTemplateMapper;

    private final AuditEventServiceInterface
            auditEventService;

    private final AuditEntityChangeServiceInterface
            auditEntityChangeService;

    public NotificationTemplateService(
            NotificationTemplateRepository notificationTemplateRepository,
            NotificationTemplateMapper notificationTemplateMapper,
            AuditEventServiceInterface auditEventService,
            AuditEntityChangeServiceInterface auditEntityChangeService) {

        this.notificationTemplateRepository =
                notificationTemplateRepository;

        this.notificationTemplateMapper =
                notificationTemplateMapper;

        this.auditEventService =
                auditEventService;

        this.auditEntityChangeService =
                auditEntityChangeService;
    }

    @Override
    public NotificationTemplateResponse createNotificationTemplate(
            NotificationTemplateRequest request) {

        NotificationTemplate notificationTemplate =
                notificationTemplateMapper.toEntity(
                        request
                );

        NotificationTemplate savedNotificationTemplate =
                notificationTemplateRepository.save(
                        notificationTemplate
                );

        return notificationTemplateMapper.toResponse(
                savedNotificationTemplate
        );
    }

    @Override
    @Transactional(
            noRollbackFor = {
                    AccessDeniedException.class,
                    ResourceNotFoundException.class,
                    RequestValidationException.class
            }
    )
    public NotificationTemplateResponse updateNotificationTemplate(
            UUID notificationTemplateId,
            NotificationTemplateUpdateRequest request,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                request,
                "request is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        requireNotificationManagementPermission(
                securityContext,
                notificationTemplateId
        );

        NotificationTemplate notificationTemplate;

        try {

            notificationTemplate =
                    notificationTemplateRepository
                            .findById(
                                    notificationTemplateId
                            )
                            .orElseThrow(
                                    () ->
                                            new ResourceNotFoundException(
                                                    "Notification template not found: "
                                                            + notificationTemplateId
                                            )
                            );

        } catch (ResourceNotFoundException exception) {

            recordNotificationManagementAudit(
                    securityContext,
                    notificationTemplateId,
                    "REJECTED",
                    "NOTIFICATION_TEMPLATE_NOT_FOUND",
                    null,
                    null
            );

            throw exception;
        }

        Map<String, Object> previousValue =
                new LinkedHashMap<>();

        Map<String, Object> currentValue =
                new LinkedHashMap<>();

        if (request.getTemplateName() != null) {

            if (request.getTemplateName().isBlank()) {

                recordNotificationManagementAudit(
                        securityContext,
                        notificationTemplateId,
                        "REJECTED",
                        "INVALID_UPDATE_REQUEST",
                        null,
                        null
                );

                throw new RequestValidationException(
                        "Template name cannot be blank"
                );
            }

            previousValue.put(
                    "templateName",
                    notificationTemplate.getTemplateName()
            );

            notificationTemplate.setTemplateName(
                    request.getTemplateName()
            );

            currentValue.put(
                    "templateName",
                    notificationTemplate.getTemplateName()
            );
        }

        if (request.getSubjectTemplate() != null) {

            previousValue.put(
                    "subjectTemplate",
                    notificationTemplate.getSubjectTemplate()
            );

            notificationTemplate.setSubjectTemplate(
                    request.getSubjectTemplate()
            );

            currentValue.put(
                    "subjectTemplate",
                    notificationTemplate.getSubjectTemplate()
            );
        }

        if (request.getBodyTemplate() != null) {

            if (request.getBodyTemplate().isBlank()) {

                recordNotificationManagementAudit(
                        securityContext,
                        notificationTemplateId,
                        "REJECTED",
                        "INVALID_UPDATE_REQUEST",
                        null,
                        null
                );

                throw new RequestValidationException(
                        "Body template cannot be blank"
                );
            }

            previousValue.put(
                    "bodyTemplate",
                    notificationTemplate.getBodyTemplate()
            );

            notificationTemplate.setBodyTemplate(
                    request.getBodyTemplate()
            );

            currentValue.put(
                    "bodyTemplate",
                    notificationTemplate.getBodyTemplate()
            );
        }

        if (request.getStatus() != null) {

            if (request.getStatus().isBlank()) {

                recordNotificationManagementAudit(
                        securityContext,
                        notificationTemplateId,
                        "REJECTED",
                        "INVALID_UPDATE_REQUEST",
                        null,
                        null
                );

                throw new RequestValidationException(
                        "Status cannot be blank"
                );
            }

            previousValue.put(
                    "status",
                    notificationTemplate.getStatus()
            );

            notificationTemplate.setStatus(
                    request.getStatus()
            );

            currentValue.put(
                    "status",
                    notificationTemplate.getStatus()
            );
        }

        if (currentValue.isEmpty()) {

            recordNotificationManagementAudit(
                    securityContext,
                    notificationTemplateId,
                    "REJECTED",
                    "INVALID_UPDATE_REQUEST",
                    null,
                    null
            );

            throw new RequestValidationException(
                    "At least one notification template field is required for update"
            );
        }

        notificationTemplate.setUpdatedAt(
                LocalDateTime.now()
        );

        try {

            NotificationTemplate savedNotificationTemplate =
                    notificationTemplateRepository
                            .saveAndFlush(
                                    notificationTemplate
                            );

            AuditEventResponse auditEvent =
                    recordNotificationManagementAudit(
                            securityContext,
                            notificationTemplateId,
                            "SUCCESS",
                            null,
                            currentValue,
                            null
                    );

            AuditEntityChangeRequest entityChangeRequest =
                    new AuditEntityChangeRequest();

            entityChangeRequest.setAuditEventId(
                    auditEvent.getAuditEventId()
            );

            entityChangeRequest.setEntityType(
                    NOTIFICATION_MANAGEMENT_ENTITY_TYPE
            );

            entityChangeRequest.setEntityId(
                    notificationTemplateId
            );

            entityChangeRequest.setOperation(
                    NOTIFICATION_MANAGEMENT_ACTION
            );

            entityChangeRequest.setPreviousValue(
                    new LinkedHashMap<>(
                            previousValue
                    )
            );

            entityChangeRequest.setCurrentValue(
                    new LinkedHashMap<>(
                            currentValue
                    )
            );

            auditEntityChangeService
                    .createAuditEntityChange(
                            entityChangeRequest
                    );

            return notificationTemplateMapper.toResponse(
                    savedNotificationTemplate
            );

        } catch (RuntimeException exception) {

            recordNotificationManagementFailureAudit(
                    securityContext,
                    notificationTemplateId,
                    currentValue,
                    exception
            );

            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplateResponse getNotificationTemplateById(
            UUID notificationTemplateId) {

        NotificationTemplate notificationTemplate =
                notificationTemplateRepository
                        .findById(notificationTemplateId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Notification template not found: "
                                                        + notificationTemplateId
                                        )
                        );

        return notificationTemplateMapper.toResponse(
                notificationTemplate
        );
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplateResponse getNotificationTemplateByScope(
            UUID organizationId,
            UUID tenantId,
            String templateCode,
            String channel,
            UUID languageId) {

        NotificationTemplate notificationTemplate =
                notificationTemplateRepository
                        .findByOrganizationIdAndTenantIdAndTemplateCodeAndChannelAndLanguageId(
                                organizationId,
                                tenantId,
                                templateCode,
                                channel,
                                languageId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Notification template not found for requested scope"
                                        )
                        );

        return notificationTemplateMapper.toResponse(
                notificationTemplate
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse>
            getNotificationTemplatesByScope(
                    UUID organizationId,
                    UUID tenantId) {

        return notificationTemplateRepository
                .findByOrganizationIdAndTenantIdOrderByTemplateNameAsc(
                        organizationId,
                        tenantId
                )
                .stream()
                .map(notificationTemplateMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse>
            getNotificationTemplatesByScopeAndStatus(
                    UUID organizationId,
                    UUID tenantId,
                    String status) {

        return notificationTemplateRepository
                .findByOrganizationIdAndTenantIdAndStatusOrderByTemplateNameAsc(
                        organizationId,
                        tenantId,
                        status
                )
                .stream()
                .map(notificationTemplateMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse>
            getNotificationTemplatesByCode(
                    String templateCode) {

        return notificationTemplateRepository
                .findByTemplateCodeOrderByTemplateNameAsc(
                        templateCode
                )
                .stream()
                .map(notificationTemplateMapper::toResponse)
                .toList();
    }

    private void requireNotificationManagementPermission(
            SecurityContext securityContext,
            UUID notificationTemplateId) {

        if (!securityContext.hasPermission(
                NOTIFICATION_MANAGE_PERMISSION
        )) {

            recordNotificationManagementAudit(
                    securityContext,
                    notificationTemplateId,
                    "REJECTED",
                    "MISSING_PERMISSION",
                    null,
                    null
            );

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + NOTIFICATION_MANAGE_PERMISSION
            );
        }
    }

    private AuditEventResponse recordNotificationManagementAudit(
            SecurityContext securityContext,
            UUID notificationTemplateId,
            String eventResult,
            String reason,
            Map<String, Object> currentValue,
            RuntimeException exception) {

        AuditEventRequest request =
                buildNotificationManagementAuditRequest(
                        securityContext,
                        notificationTemplateId,
                        eventResult,
                        reason,
                        currentValue,
                        exception
                );

        return auditEventService.createAuditEvent(
                request
        );
    }

    private void recordNotificationManagementFailureAudit(
            SecurityContext securityContext,
            UUID notificationTemplateId,
            Map<String, Object> currentValue,
            RuntimeException exception) {

        AuditEventRequest request =
                buildNotificationManagementAuditRequest(
                        securityContext,
                        notificationTemplateId,
                        "FAILURE",
                        "NOTIFICATION_MANAGEMENT_FAILED",
                        currentValue,
                        exception
                );

        auditEventService
                .createAuditEventRequiresNew(
                        request
                );
    }

    private AuditEventRequest buildNotificationManagementAuditRequest(
            SecurityContext securityContext,
            UUID notificationTemplateId,
            String eventResult,
            String reason,
            Map<String, Object> currentValue,
            RuntimeException exception) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setTenantId(
                securityContext.getTenantId()
        );

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                NOTIFICATION_MANAGEMENT_EVENT_TYPE
        );

        request.setEntityType(
                NOTIFICATION_MANAGEMENT_ENTITY_TYPE
        );

        request.setEntityId(
                notificationTemplateId
        );

        request.setAction(
                NOTIFICATION_MANAGEMENT_ACTION
        );

        request.setSourceComponent(
                NOTIFICATION_MANAGEMENT_SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                NOTIFICATION_MANAGE_PERMISSION
        );

        if (currentValue != null
                && !currentValue.isEmpty()) {

            details.put(
                    "fields",
                    new ArrayList<>(
                            currentValue.keySet()
                    )
            );
        }

        if (reason != null) {

            details.put(
                    "reason",
                    reason
            );
        }

        if (exception != null) {

            details.put(
                    "errorType",
                    exception.getClass().getName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        return request;
    }
}