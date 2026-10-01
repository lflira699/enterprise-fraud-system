package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Permission;
import com.efs.modules.administration.entity.Role;
import com.efs.modules.administration.entity.RolePermission;
import com.efs.modules.administration.repository.PermissionRepository;
import com.efs.modules.administration.repository.RolePermissionRepository;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RolePermissionAssignmentService {

    private static final String PERMISSION_VIEW =
            "role.view";

    private static final String PERMISSION_MANAGE =
            "role.manage";

    private static final String EVENT_TYPE_ROLE_PERMISSION_ASSIGNMENT =
            "ROLE_PERMISSION_ASSIGNMENT";

    private static final String ENTITY_TYPE_ROLE_PERMISSION =
            "ROLE_PERMISSION";

    private static final String SOURCE_COMPONENT_ADMINISTRATION =
            "ADMINISTRATION";

    private static final String ACTION_ASSIGN =
            "ASSIGN";

    private static final String ACTION_REMOVE =
            "REMOVE";

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

    private final RolePermissionRepository
            rolePermissionRepository;

    private final RoleRepository
            roleRepository;

    private final PermissionRepository
            permissionRepository;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final AuditEventServiceInterface
            auditEventService;

    public RolePermissionAssignmentService(
            RolePermissionRepository rolePermissionRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserAccountLookupServiceInterface userAccountLookupService,
            AuditEventServiceInterface auditEventService) {

        this.rolePermissionRepository =
                rolePermissionRepository;

        this.roleRepository =
                roleRepository;

        this.permissionRepository =
                permissionRepository;

        this.userAccountLookupService =
                userAccountLookupService;

        this.auditEventService =
                auditEventService;
    }

    @Transactional
    public RolePermission assignPermission(
            UUID roleId,
            UUID permissionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_MANAGE
                    );

            validateIdentifier(
                    roleId,
                    "Role id"
            );

            validateIdentifier(
                    permissionId,
                    "Permission id"
            );

            Role role =
                    getRole(
                            roleId
                    );

            validateInstitutionalRoleScope(
                    role,
                    actor
            );

            getPermission(
                    permissionId
            );

            if (
                    rolePermissionRepository
                            .existsByRoleIdAndPermissionId(
                                    roleId,
                                    permissionId
                            )
            ) {
                throw new DuplicateRecordException(
                        "Role permission assignment already exists"
                );
            }

            RolePermission assignment =
                    new RolePermission();

            assignment.setRoleId(
                    roleId
            );

            assignment.setPermissionId(
                    permissionId
            );

            assignment.setGrantedBy(
                    actor.userId()
            );

            RolePermission saved =
                    rolePermissionRepository.save(
                            assignment
                    );

            recordAudit(
                    securityContext,
                    actor,
                    saved.getRolePermissionId(),
                    ACTION_ASSIGN,
                    RESULT_SUCCESS,
                    null,
                    null,
                    assignmentDetails(
                            roleId,
                            permissionId,
                            null
                    )
            );

            return saved;
        }
        catch (
                ResourceNotFoundException
                        | DuplicateRecordException
                        | AccessDeniedException
                        | IllegalArgumentException exception
        ) {

            recordAudit(
                    securityContext,
                    actor,
                    null,
                    ACTION_ASSIGN,
                    RESULT_REJECTED,
                    auditReason(exception),
                    exception,
                    assignmentDetails(
                            roleId,
                            permissionId,
                            null
                    )
            );

            throw exception;
        }
        catch (RuntimeException exception) {

            recordAudit(
                    securityContext,
                    actor,
                    null,
                    ACTION_ASSIGN,
                    RESULT_FAILURE,
                    null,
                    exception,
                    assignmentDetails(
                            roleId,
                            permissionId,
                            null
                    )
            );

            throw exception;
        }
    }

    @Transactional
    public void removePermission(
            UUID roleId,
            UUID permissionId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                null;

        UUID assignmentId =
                null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_MANAGE
                    );

            validateIdentifier(
                    roleId,
                    "Role id"
            );

            validateIdentifier(
                    permissionId,
                    "Permission id"
            );

            Role role =
                    getRole(
                            roleId
                    );

            validateInstitutionalRoleScope(
                    role,
                    actor
            );

            getPermission(
                    permissionId
            );

            RolePermission assignment =
                    rolePermissionRepository
                            .findByRoleIdAndPermissionId(
                                    roleId,
                                    permissionId
                            )
                            .orElseThrow(
                                    () ->
                                            new ResourceNotFoundException(
                                                    "Role permission assignment not found"
                                            )
                            );

            assignmentId =
                    assignment.getRolePermissionId();

            rolePermissionRepository.delete(
                    assignment
            );

            recordAudit(
                    securityContext,
                    actor,
                    assignmentId,
                    ACTION_REMOVE,
                    RESULT_SUCCESS,
                    null,
                    null,
                    assignmentDetails(
                            roleId,
                            permissionId,
                            null
                    )
            );
        }
        catch (
                ResourceNotFoundException
                        | AccessDeniedException
                        | IllegalArgumentException exception
        ) {

            recordAudit(
                    securityContext,
                    actor,
                    assignmentId,
                    ACTION_REMOVE,
                    RESULT_REJECTED,
                    auditReason(exception),
                    exception,
                    assignmentDetails(
                            roleId,
                            permissionId,
                            null
                    )
            );

            throw exception;
        }
        catch (RuntimeException exception) {

            recordAudit(
                    securityContext,
                    actor,
                    assignmentId,
                    ACTION_REMOVE,
                    RESULT_FAILURE,
                    null,
                    exception,
                    assignmentDetails(
                            roleId,
                            permissionId,
                            null
                    )
            );

            throw exception;
        }
    }

    public List<RolePermission> getRolePermissions(
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

            validateIdentifier(
                    roleId,
                    "Role id"
            );

            Role role =
                    getRole(
                            roleId
                    );

            validateInstitutionalRoleScope(
                    role,
                    actor
            );

            List<RolePermission> assignments =
                    rolePermissionRepository
                            .findByRoleId(
                                    roleId
                            );

            recordAudit(
                    securityContext,
                    actor,
                    null,
                    ACTION_VIEW,
                    RESULT_SUCCESS,
                    null,
                    null,
                    assignmentDetails(
                            roleId,
                            null,
                            assignments.size()
                    )
            );

            return assignments;
        }
        catch (
                ResourceNotFoundException
                        | AccessDeniedException
                        | IllegalArgumentException exception
        ) {

            recordAudit(
                    securityContext,
                    actor,
                    null,
                    ACTION_VIEW,
                    RESULT_REJECTED,
                    auditReason(exception),
                    exception,
                    assignmentDetails(
                            roleId,
                            null,
                            null
                    )
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
                    assignmentDetails(
                            roleId,
                            null,
                            null
                    )
            );

            throw exception;
        }
    }

    private Role getRole(
            UUID roleId) {

        return roleRepository
                .findById(
                        roleId
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Role not found: "
                                                + roleId
                                )
                );
    }

    private Permission getPermission(
            UUID permissionId) {

        return permissionRepository
                .findById(
                        permissionId
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Permission not found: "
                                                + permissionId
                                )
                );
    }

    private void validateInstitutionalRoleScope(
            Role role,
            UserAccountReference actor) {

        if (
                Boolean.TRUE.equals(
                        role.getSystem()
                )
        ) {
            throw new AccessDeniedException(
                    "System role is not available to institutional role permission assignment"
            );
        }

        if (
                role.getOrganizationId() == null
                        || !actor.organizationId()
                        .equals(
                                role.getOrganizationId()
                        )
        ) {
            throw new AccessDeniedException(
                    "Role does not belong to the authorized organization"
            );
        }
    }

    private UserAccountReference authorize(
            SecurityContext securityContext,
            String permissionCode) {

        if (
                securityContext == null
                        || !securityContext.hasPermission(
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

        UserAccountReference actor =
                userAccountLookupService
                        .getAuthorizedUser(
                                userId
                        );

        if (
                actor == null
                        || actor.organizationId() == null
        ) {
            throw new AccessDeniedException(
                    "Authorized organization is required"
            );
        }

        return actor;
    }

    private void validateIdentifier(
            UUID identifier,
            String fieldName) {

        if (identifier == null) {
            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
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

    private Map<String, Object> assignmentDetails(
            UUID roleId,
            UUID permissionId,
            Integer resultCount) {

        Map<String, Object> details =
                new LinkedHashMap<>();

        if (roleId != null) {
            details.put(
                    "roleId",
                    roleId
            );
        }

        if (permissionId != null) {
            details.put(
                    "permissionId",
                    permissionId
            );
        }

        if (resultCount != null) {
            details.put(
                    "resultCount",
                    resultCount
            );
        }

        return details;
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
        else if (securityContext != null) {

            request.setUserId(
                    securityContext.getUserId()
            );
        }

        if (securityContext != null) {

            request.setSessionId(
                    securityContext.getSessionId()
            );
        }

        request.setEventType(
                EVENT_TYPE_ROLE_PERMISSION_ASSIGNMENT
        );

        request.setEntityType(
                ENTITY_TYPE_ROLE_PERMISSION
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