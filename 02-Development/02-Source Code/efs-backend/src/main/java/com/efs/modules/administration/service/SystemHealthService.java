package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.SystemHealthComponentResponse;
import com.efs.modules.administration.dto.SystemHealthResponse;
import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.security.SecurityContext;
import org.springframework.boot.actuate.health.CompositeHealth;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SystemHealthService
        implements SystemHealthServiceInterface {

    private static final String HEALTH_VIEW_PERMISSION =
            "health.view";

    private static final String EVENT_TYPE =
            "SYSTEM_HEALTH_MONITORING";

    private static final String ENTITY_TYPE =
            "SYSTEM_HEALTH";

    private static final String ACTION =
            "VIEW";

    private static final String SOURCE_COMPONENT =
            "ADMINISTRATION";

    private static final String MISSING_PERMISSION_REASON =
            "MISSING_PERMISSION";

    private static final String INFORMATION_UNAVAILABLE_REASON =
            "HEALTH_INFORMATION_UNAVAILABLE";

    private static final String PROCESSING_FAILURE_REASON =
            "SYSTEM_HEALTH_MONITORING_FAILED";

    private static final String INFORMATION_COMPLETE =
            "COMPLETE";

    private static final String INFORMATION_PARTIAL =
            "PARTIAL";

    private static final String INFORMATION_UNAVAILABLE =
            "UNAVAILABLE";

    private final HealthEndpoint
            healthEndpoint;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final AuditEventServiceInterface
            auditEventService;

    public SystemHealthService(
            HealthEndpoint healthEndpoint,
            UserAccountLookupServiceInterface
                    userAccountLookupService,
            AuditEventServiceInterface
                    auditEventService) {

        this.healthEndpoint =
                healthEndpoint;

        this.userAccountLookupService =
                userAccountLookupService;

        this.auditEventService =
                auditEventService;
    }

    @Override
    @Transactional(readOnly = true)
    public SystemHealthResponse getSystemHealth(
            SecurityContext securityContext) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        requireHealthViewPermission(
                securityContext
        );

        UserAccountReference authorizedUser =
                null;

        try {

            authorizedUser =
                    userAccountLookupService
                            .getAuthorizedUser(
                                    securityContext
                                            .getUserId()
                            );

            HealthComponent health =
                    healthEndpoint.health();

            if (health == null) {

                SystemHealthResponse response =
                        unavailableResponse();

                recordAudit(
                        securityContext,
                        authorizedUser,
                        "FAILURE",
                        INFORMATION_UNAVAILABLE_REASON,
                        response,
                        null
                );

                return response;
            }

            ExtractionResult extraction =
                    extractComponents(
                            health
                    );

            String informationStatus =
                    extraction.partial()
                            ? INFORMATION_PARTIAL
                            : INFORMATION_COMPLETE;

            String status =
                    resolveStatus(
                            health
                    );

            if (
                    Status.UNKNOWN.getCode()
                            .equals(status)
            ) {
                informationStatus =
                        INFORMATION_PARTIAL;
            }

            SystemHealthResponse response =
                    new SystemHealthResponse(
                            status,
                            informationStatus,
                            extraction.components(),
                            LocalDateTime.now()
                    );

            recordAudit(
                    securityContext,
                    authorizedUser,
                    "SUCCESS",
                    null,
                    response,
                    null
            );

            return response;

        } catch (RuntimeException exception) {

            recordAudit(
                    securityContext,
                    authorizedUser,
                    "FAILURE",
                    PROCESSING_FAILURE_REASON,
                    null,
                    exception
            );

            throw exception;
        }
    }

    private void requireHealthViewPermission(
            SecurityContext securityContext) {

        if (
                securityContext.hasPermission(
                        HEALTH_VIEW_PERMISSION
                )
        ) {
            return;
        }

        recordAudit(
                securityContext,
                null,
                "REJECTED",
                MISSING_PERMISSION_REASON,
                null,
                null
        );

        throw new AccessDeniedException(
                "Missing permission: "
                        + HEALTH_VIEW_PERMISSION
        );
    }

    private ExtractionResult extractComponents(
            HealthComponent health) {

        if (!(health instanceof CompositeHealth compositeHealth)) {

            return new ExtractionResult(
                    List.of(),
                    false
            );
        }

        Map<String, HealthComponent> ordered =
                compositeHealth.getComponents();

        List<SystemHealthComponentResponse> components =
                new ArrayList<>();

        boolean partial =
                false;

        for (
                Map.Entry<String, HealthComponent> entry
                        : ordered.entrySet()
        ) {

            HealthComponent component =
                    entry.getValue();

            if (component == null) {
                partial = true;
                continue;
            }

            String componentStatus =
                    resolveStatus(
                            component
                    );

            if (
                    Status.UNKNOWN.getCode()
                            .equals(componentStatus)
            ) {
                partial = true;
            }

            components.add(
                    new SystemHealthComponentResponse(
                            entry.getKey(),
                            componentStatus
                    )
            );
        }

        return new ExtractionResult(
                components,
                partial
        );
    }

    private String resolveStatus(
            HealthComponent health) {

        if (
                health == null
                        || health.getStatus() == null
                        || health.getStatus().getCode() == null
                        || health.getStatus()
                                .getCode()
                                .isBlank()
        ) {
            return Status.UNKNOWN.getCode();
        }

        return health.getStatus()
                .getCode();
    }

    private SystemHealthResponse unavailableResponse() {

        return new SystemHealthResponse(
                Status.UNKNOWN.getCode(),
                INFORMATION_UNAVAILABLE,
                List.of(),
                LocalDateTime.now()
        );
    }

    private void recordAudit(
            SecurityContext securityContext,
            UserAccountReference authorizedUser,
            String eventResult,
            String reason,
            SystemHealthResponse response,
            RuntimeException exception) {

        AuditEventRequest request =
                new AuditEventRequest();

        if (authorizedUser != null) {

            request.setOrganizationId(
                    authorizedUser.organizationId()
            );

            request.setTenantId(
                    authorizedUser.tenantId()
            );
        }

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                EVENT_TYPE
        );

        request.setEntityType(
                ENTITY_TYPE
        );

        request.setEntityId(
                null
        );

        request.setAction(
                ACTION
        );

        request.setSourceComponent(
                SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "permissionCode",
                HEALTH_VIEW_PERMISSION
        );

        if (response != null) {

            details.put(
                    "status",
                    response.getStatus()
            );

            details.put(
                    "informationStatus",
                    response.getInformationStatus()
            );

            details.put(
                    "componentCount",
                    response.getComponents()
                            .size()
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
                    exception.getClass()
                            .getName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        auditEventService
                .createAuditEventRequiresNew(
                        request
                );
    }

    private record ExtractionResult(
            List<SystemHealthComponentResponse> components,
            boolean partial) {
    }
}