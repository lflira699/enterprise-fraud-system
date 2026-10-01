package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Role;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RoleManagementService {

    private static final String PERMISSION_MANAGE =
            "role.manage";

    private static final String PERMISSION_VIEW =
            "role.view";

    private static final String EVENT_TYPE_ROLE_MANAGEMENT =
            "ROLE_MANAGEMENT";

    private static final String ENTITY_TYPE_ROLE =
            "ROLE";

    private static final String SOURCE_COMPONENT_ADMINISTRATION =
            "ADMINISTRATION";

    private static final String ACTION_CREATE =
            "CREATE";

    private static final String ACTION_UPDATE =
            "UPDATE";

    private static final String ACTION_VIEW =
            "VIEW";

    private static final String ACTION_ENABLE =
            "ENABLE";

    private static final String ACTION_DISABLE =
            "DISABLE";

    private static final String RESULT_SUCCESS =
            "SUCCESS";

    private static final String RESULT_REJECTED =
            "REJECTED";

    private static final String RESULT_FAILURE =
            "FAILURE";

    private static final String MISSING_PERMISSION_REASON =
            "MISSING_PERMISSION";

    private final RoleRepository roleRepository;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final AuditEventServiceInterface
            auditEventService;

    public RoleManagementService(
            RoleRepository roleRepository,
            UserAccountLookupServiceInterface userAccountLookupService,
            AuditEventServiceInterface auditEventService) {

        this.roleRepository =
                roleRepository;

        this.userAccountLookupService =
                userAccountLookupService;

        this.auditEventService =
                auditEventService;
    }

    public Role createRole(
            String roleCode,
            String roleName,
            String description,
            SecurityContext securityContext) {

        UserAccountReference actor =
                null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_MANAGE
                    );

            validateRequired(
                    roleCode,
                    "Role code",
                    60
            );

            validateRequired(
                    roleName,
                    "Role name",
                    120
            );

            if (
                    roleRepository.existsByRoleCode(
                            roleCode
                    )
            ) {
                throw new DuplicateRecordException(
                        "Role code already exists: "
                                + roleCode
                );
            }

            if (
                    roleRepository
                            .existsByRoleNameAndOrganizationId(
                                    roleName,
                                    actor.organizationId()
                            )
            ) {
                throw new DuplicateRecordException(
                        "Role name already exists: "
                                + roleName
                );
            }

            Role role =
                    new Role();

            role.setOrganizationId(
                    actor.organizationId()
            );

            role.setRoleCode(
                    roleCode
            );

            role.setRoleName(
                    roleName
            );

            role.setDescription(
                    description
            );

            role.setSystem(
                    false
            );

            role.setStatus(
                    "ACTIVE"
            );

            role.setCreatedAt(
                    LocalDateTime.now()
            );

            Role saved =
                    roleRepository.save(
                            role
                    );

            recordAudit(
                    securityContext,
                    actor,
                    saved.getRoleId(),
                    ACTION_CREATE,
                    RESULT_SUCCESS,
                    null,
                    null
            );

            return saved;

        } catch (
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
                    exception
            );

            throw exception;

        } catch (RuntimeException exception) {

            recordAudit(
                    securityContext,
                    actor,
                    null,
                    ACTION_CREATE,
                    RESULT_FAILURE,
                    null,
                    exception
            );

            throw exception;
        }
    }
    public Role updateRole(
            UUID roleId,
            String roleCode,
            String roleName,
            String description,
            SecurityContext securityContext) {

        UserAccountReference actor =
                null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_MANAGE
                    );

            validateRequired(
                    roleCode,
                    "Role code",
                    60
            );

            validateRequired(
                    roleName,
                    "Role name",
                    120
            );

            Role role =
                    roleRepository.findById(
                            roleId
                    ).orElseThrow(
                            () ->
                                    new ResourceNotFoundException(
                                            "Role not found: "
                                                    + roleId
                                    )
                    );

            if (
                    !actor.organizationId()
                            .equals(
                                    role.getOrganizationId()
                            )
            ) {
                throw new AccessDeniedException(
                        "Role does not belong to the authorized organization"
                );
            }

            if (
                    roleRepository
                            .existsByRoleCodeAndRoleIdNot(
                                    roleCode,
                                    roleId
                            )
            ) {
                throw new DuplicateRecordException(
                        "Role code already exists: "
                                + roleCode
                );
            }

            if (
                    roleRepository
                            .existsByRoleNameAndOrganizationIdAndRoleIdNot(
                                    roleName,
                                    actor.organizationId(),
                                    roleId
                            )
            ) {
                throw new DuplicateRecordException(
                        "Role name already exists: "
                                + roleName
                );
            }

            role.setRoleCode(
                    roleCode
            );

            role.setRoleName(
                    roleName
            );

            role.setDescription(
                    description
            );

            Role saved =
                    roleRepository.save(
                            role
                    );

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_UPDATE,
                    RESULT_SUCCESS,
                    null,
                    null
            );

            return saved;

        } catch (
                ResourceNotFoundException
                        | DuplicateRecordException
                        | AccessDeniedException
                        | IllegalArgumentException exception
        ) {

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_UPDATE,
                    RESULT_REJECTED,
                    auditReason(exception),
                    exception
            );

            throw exception;

        } catch (RuntimeException exception) {

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_UPDATE,
                    RESULT_FAILURE,
                    null,
                    exception
            );

            throw exception;
        }
    }
    public Role enableRole(
            UUID roleId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_MANAGE
                    );

            Role role =
                    roleRepository.findById(
                            roleId
                    ).orElseThrow(
                            () ->
                                    new ResourceNotFoundException(
                                            "Role not found: "
                                                    + roleId
                                    )
                    );

            if (
                    !actor.organizationId()
                            .equals(
                                    role.getOrganizationId()
                            )
            ) {
                throw new AccessDeniedException(
                        "Role does not belong to the authorized organization"
                );
            }

            role.enable();

            Role saved =
                    roleRepository.save(
                            role
                    );

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_ENABLE,
                    RESULT_SUCCESS,
                    null,
                    null
            );

            return saved;

        } catch (
                ResourceNotFoundException
                        | AccessDeniedException exception
        ) {

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_ENABLE,
                    RESULT_REJECTED,
                    auditReason(exception),
                    exception
            );

            throw exception;

        } catch (RuntimeException exception) {

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_ENABLE,
                    RESULT_FAILURE,
                    null,
                    exception
            );

            throw exception;
        }
    }
    public Role disableRole(
            UUID roleId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_MANAGE
                    );

            Role role =
                    roleRepository.findById(
                            roleId
                    ).orElseThrow(
                            () ->
                                    new ResourceNotFoundException(
                                            "Role not found: "
                                                    + roleId
                                    )
                    );

            if (
                    !actor.organizationId()
                            .equals(
                                    role.getOrganizationId()
                            )
            ) {
                throw new AccessDeniedException(
                        "Role does not belong to the authorized organization"
                );
            }

            role.disable();

            Role saved =
                    roleRepository.save(
                            role
                    );

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_DISABLE,
                    RESULT_SUCCESS,
                    null,
                    null
            );

            return saved;

        } catch (
                ResourceNotFoundException
                        | AccessDeniedException exception
        ) {

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_DISABLE,
                    RESULT_REJECTED,
                    auditReason(exception),
                    exception
            );

            throw exception;

        } catch (RuntimeException exception) {

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_DISABLE,
                    RESULT_FAILURE,
                    null,
                    exception
            );

            throw exception;
        }
    }
    public Role getRole(
            UUID roleId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_VIEW
                    );

            Role role =
                    roleRepository.findById(
                            roleId
                    ).orElseThrow(
                            () ->
                                    new ResourceNotFoundException(
                                            "Role not found: "
                                                    + roleId
                                    )
                    );

            if (
                    !actor.organizationId()
                            .equals(
                                    role.getOrganizationId()
                            )
            ) {
                throw new AccessDeniedException(
                        "Role does not belong to the authorized organization"
                );
            }

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_VIEW,
                    RESULT_SUCCESS,
                    null,
                    null
            );

            return role;

        } catch (
                ResourceNotFoundException
                        | AccessDeniedException exception
        ) {

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_VIEW,
                    RESULT_REJECTED,
                    auditReason(exception),
                    exception
            );

            throw exception;

        } catch (RuntimeException exception) {

            recordAudit(
                    securityContext,
                    actor,
                    roleId,
                    ACTION_VIEW,
                    RESULT_FAILURE,
                    null,
                    exception
            );

            throw exception;
        }
    }
    private String auditReason(
            RuntimeException exception) {

        if (
                exception instanceof AccessDeniedException
                        && exception.getMessage() != null
                        && exception.getMessage().startsWith(
                                "Missing permission: "
                        )
        ) {
            return MISSING_PERMISSION_REASON;
        }

        return null;
    }

    private void recordAudit(
            SecurityContext securityContext,
            UserAccountReference actor,
            UUID entityId,
            String action,
            String eventResult,
            String reason,
            RuntimeException exception) {

        AuditEventRequest request =
                new AuditEventRequest();

        if (actor != null) {
            request.setOrganizationId(
                    actor.organizationId()
            );

            request.setTenantId(
                    actor.tenantId()
            );
        }

        if (securityContext != null) {
            request.setUserId(
                    securityContext.getUserId()
            );

            request.setSessionId(
                    securityContext.getSessionId()
            );
        }

        request.setEventType(
                EVENT_TYPE_ROLE_MANAGEMENT
        );

        request.setEntityType(
                ENTITY_TYPE_ROLE
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
                eventResult
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        if (reason != null) {
            details.put(
                    "reason",
                    reason
            );
        }

        if (exception != null) {
            details.put(
                    "errorType",
                    exception.getClass().getSimpleName()
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
    private UserAccountReference authorize(
            SecurityContext securityContext,
            String permissionCode) {

        if (
                securityContext == null
                        ||
                !securityContext.hasPermission(
                        permissionCode
                )
        ) {
            throw new AccessDeniedException(
                    "Missing permission: "
                            + permissionCode
            );
        }

        UUID userId =
                securityContext.getUserId();

        UserAccountReference authorizedUser =
                userAccountLookupService
                        .getAuthorizedUser(
                                userId
                        );

        if (
                authorizedUser == null
                        ||
                authorizedUser.organizationId()
                        == null
        ) {
            throw new AccessDeniedException(
                    "Authorized organization is required"
            );
        }

        return authorizedUser;
    }

    private void validateRequired(
            String value,
            String fieldName,
            int maxLength) {

        if (
                value == null
                        ||
                value.isBlank()
        ) {
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
}