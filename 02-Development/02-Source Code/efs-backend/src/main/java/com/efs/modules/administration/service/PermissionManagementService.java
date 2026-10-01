package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Permission;
import com.efs.modules.administration.repository.PermissionRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PermissionManagementService {

    private static final String PERMISSION_VIEW =
            "permission.view";

    private static final String PERMISSION_MANAGE =
            "permission.manage";

    private static final String EVENT_TYPE_PERMISSION_MANAGEMENT =
            "PERMISSION_MANAGEMENT";

    private static final String ENTITY_TYPE_PERMISSION =
            "PERMISSION";

    private static final String SOURCE_COMPONENT_ADMINISTRATION =
            "ADMINISTRATION";

    private static final String ACTION_CREATE =
            "CREATE";

    private static final String ACTION_UPDATE =
            "UPDATE";

    private static final String ACTION_VIEW =
            "VIEW";

    private static final String RESULT_SUCCESS =
            "SUCCESS";

    private static final String RESULT_REJECTED =
            "REJECTED";

    private static final String RESULT_FAILURE =
            "FAILURE";

    private static final String MISSING_PERMISSION_REASON =
            "MISSING_PERMISSION";

    private final PermissionRepository permissionRepository;

    private final UserAccountLookupServiceInterface userAccountLookupService;

    private final AuditEventServiceInterface auditEventService;

    public PermissionManagementService(
            PermissionRepository permissionRepository,
            UserAccountLookupServiceInterface userAccountLookupService,
            AuditEventServiceInterface auditEventService) {

        this.permissionRepository =
                permissionRepository;

        this.userAccountLookupService =
                userAccountLookupService;

        this.auditEventService =
                auditEventService;
    }

    @Transactional
    public Permission createPermission(
            String permissionCode,
            String permissionName,
            String resource,
            String action,
            String description,
            SecurityContext securityContext) {

        UserAccountReference actor = null;

        try {
            actor = authorize(
                    securityContext,
                    PERMISSION_MANAGE
            );

            validateRequired(
                    permissionCode,
                    "Permission code",
                    80
            );

            validateRequired(
                    permissionName,
                    "Permission name",
                    150
            );

            validateRequired(
                    resource,
                    "Resource",
                    80
            );

            validateRequired(
                    action,
                    "Action",
                    60
            );

            if (permissionRepository.existsByPermissionCode(permissionCode)) {
                throw new DuplicateRecordException(
                        "Permission code already exists: "
                                + permissionCode
                );
            }

            Permission permission =
                    new Permission();

            permission.setPermissionCode(
                    permissionCode
            );

            permission.setPermissionName(
                    permissionName
            );

            permission.setResource(
                    resource
            );

            permission.setAction(
                    action
            );

            permission.setDescription(
                    description
            );

            Permission saved =
                    permissionRepository.save(
                            permission
                    );

            recordAudit(
                    securityContext,
                    actor,
                    saved.getPermissionId(),
                    ACTION_CREATE,
                    RESULT_SUCCESS,
                    null,
                    null,
                    null
            );

            return saved;
        }
        catch (
                DuplicateRecordException
                        | AccessDeniedException
                        | IllegalArgumentException exception
        ) {
            recordAudit(
                    securityContext,
                    actor,
                    null,
                    ACTION_CREATE,
                    RESULT_REJECTED,
                    auditReason(exception),
                    exception,
                    null
            );

            throw exception;
        }
        catch (RuntimeException exception) {
            recordAudit(
                    securityContext,
                    actor,
                    null,
                    ACTION_CREATE,
                    RESULT_FAILURE,
                    null,
                    exception,
                    null
            );

            throw exception;
        }
    }

    @Transactional
    public Permission updatePermission(
            UUID permissionId,
            String permissionName,
            String description,
            SecurityContext securityContext) {

        UserAccountReference actor = null;

        try {
            actor = authorize(
                    securityContext,
                    PERMISSION_MANAGE
            );

            if (permissionId == null) {
                throw new IllegalArgumentException(
                        "Permission id is required"
                );
            }

            validateRequired(
                    permissionName,
                    "Permission name",
                    150
            );

            Permission permission =
                    permissionRepository
                            .findById(permissionId)
                            .orElseThrow(
                                    () ->
                                            new ResourceNotFoundException(
                                                    "Permission not found: "
                                                            + permissionId
                                            )
                            );

            permission.setPermissionName(
                    permissionName
            );

            permission.setDescription(
                    description
            );

            Permission saved =
                    permissionRepository.save(
                            permission
                    );

            recordAudit(
                    securityContext,
                    actor,
                    permissionId,
                    ACTION_UPDATE,
                    RESULT_SUCCESS,
                    null,
                    null,
                    null
            );

            return saved;
        }
        catch (
                ResourceNotFoundException
                        | AccessDeniedException
                        | IllegalArgumentException exception
        ) {
            recordAudit(
                    securityContext,
                    actor,
                    permissionId,
                    ACTION_UPDATE,
                    RESULT_REJECTED,
                    auditReason(exception),
                    exception,
                    null
            );

            throw exception;
        }
        catch (RuntimeException exception) {
            recordAudit(
                    securityContext,
                    actor,
                    permissionId,
                    ACTION_UPDATE,
                    RESULT_FAILURE,
                    null,
                    exception,
                    null
            );

            throw exception;
        }
    }

    public Permission getPermission(
            UUID permissionId,
            SecurityContext securityContext) {

        UserAccountReference actor = null;

        try {
            actor = authorize(
                    securityContext,
                    PERMISSION_VIEW
            );

            if (permissionId == null) {
                throw new IllegalArgumentException(
                        "Permission id is required"
                );
            }

            Permission permission =
                    permissionRepository
                            .findById(permissionId)
                            .orElseThrow(
                                    () ->
                                            new ResourceNotFoundException(
                                                    "Permission not found: "
                                                            + permissionId
                                            )
                            );

            recordAudit(
                    securityContext,
                    actor,
                    permissionId,
                    ACTION_VIEW,
                    RESULT_SUCCESS,
                    null,
                    null,
                    null
            );

            return permission;
        }
        catch (
                ResourceNotFoundException
                        | AccessDeniedException
                        | IllegalArgumentException exception
        ) {
            recordAudit(
                    securityContext,
                    actor,
                    permissionId,
                    ACTION_VIEW,
                    RESULT_REJECTED,
                    auditReason(exception),
                    exception,
                    null
            );

            throw exception;
        }
        catch (RuntimeException exception) {
            recordAudit(
                    securityContext,
                    actor,
                    permissionId,
                    ACTION_VIEW,
                    RESULT_FAILURE,
                    null,
                    exception,
                    null
            );

            throw exception;
        }
    }

    public List<Permission> getPermissions(
            SecurityContext securityContext) {

        UserAccountReference actor = null;

        try {
            actor = authorize(
                    securityContext,
                    PERMISSION_VIEW
            );

            List<Permission> permissions =
                    permissionRepository.findAll();

            Map<String, Object> details =
                    new LinkedHashMap<>();

            details.put(
                    "resultCount",
                    permissions.size()
            );

            recordAudit(
                    securityContext,
                    actor,
                    null,
                    ACTION_VIEW,
                    RESULT_SUCCESS,
                    null,
                    null,
                    details
            );

            return permissions;
        }
        catch (AccessDeniedException exception) {
            recordAudit(
                    securityContext,
                    actor,
                    null,
                    ACTION_VIEW,
                    RESULT_REJECTED,
                    auditReason(exception),
                    exception,
                    null
            );

            throw exception;
        }
        catch (RuntimeException exception) {
            recordAudit(
                    securityContext,
                    actor,
                    null,
                    ACTION_VIEW,
                    RESULT_FAILURE,
                    null,
                    exception,
                    null
            );

            throw exception;
        }
    }

    private UserAccountReference authorize(
            SecurityContext securityContext,
            String permissionCode) {

        if (securityContext == null) {
            throw new AccessDeniedException(
                    "Missing permission: "
                            + permissionCode
            );
        }

        if (!securityContext.hasPermission(permissionCode)) {
            throw new AccessDeniedException(
                    "Missing permission: "
                            + permissionCode
            );
        }

        UUID userId =
                securityContext.getUserId();

        UserAccountReference actor =
                userAccountLookupService
                        .getAuthorizedUser(userId);

        if (actor == null) {
            throw new AccessDeniedException(
                    "Authorized organization is required"
            );
        }

        if (actor.organizationId() == null) {
            throw new AccessDeniedException(
                    "Authorized organization is required"
            );
        }

        return actor;
    }

    private void validateRequired(
            String value,
            String fieldName,
            int maxLength) {

        if (value == null) {
            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
        }

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
        }

        if (value.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName
                            + " exceeds maximum length "
                            + maxLength
            );
        }
    }

    private String auditReason(
            RuntimeException exception) {

        if (exception instanceof AccessDeniedException) {
            String message =
                    exception.getMessage();

            if (message != null) {
                if (message.startsWith("Missing permission: ")) {
                    return MISSING_PERMISSION_REASON;
                }
            }
        }

        return null;
    }

    private void recordAudit(
            SecurityContext securityContext,
            UserAccountReference actor,
            UUID entityId,
            String action,
            String result,
            String reason,
            RuntimeException exception,
            Map<String, Object> operationDetails) {

        AuditEventRequest request =
                new AuditEventRequest();

        if (actor != null) {
            request.setOrganizationId(
                    actor.organizationId()
            );

            request.setTenantId(
                    actor.tenantId()
            );

            request.setUserId(
                    actor.userId()
            );
        }
        else {
            if (securityContext != null) {
                request.setUserId(
                        securityContext.getUserId()
                );
            }
        }

        if (securityContext != null) {
            request.setSessionId(
                    securityContext.getSessionId()
            );
        }

        request.setEventType(
                EVENT_TYPE_PERMISSION_MANAGEMENT
        );

        request.setEntityType(
                ENTITY_TYPE_PERMISSION
        );

        request.setEntityId(
                entityId
        );

        request.setAction(
                action
        );

        request.setSourceComponent(
                SOURCE_COMPONENT_ADMINISTRATION
        );

        request.setEventResult(
                result
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        if (operationDetails != null) {
            details.putAll(
                    operationDetails
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
                    "exception",
                    exception
                            .getClass()
                            .getSimpleName()
            );

            if (exception.getMessage() != null) {
                details.put(
                        "errorMessage",
                        exception.getMessage()
                );
            }
        }

        request.setEventDetails(
                details
        );

        auditEventService
                .createAuditEventRequiresNew(
                        request
                );
    }
}