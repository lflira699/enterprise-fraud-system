package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Role;
import com.efs.modules.administration.entity.UserAccount;
import com.efs.modules.administration.entity.UserRole;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.modules.administration.repository.UserAccountRepository;
import com.efs.modules.administration.repository.UserRoleRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserRoleAssignmentService {

    private static final String PERMISSION_MANAGE =
            "user.manage";

    private static final String PERMISSION_VIEW =
            "user.view";

    private static final String EVENT_TYPE =
            "USER_ROLE_ASSIGNMENT";

    private static final String ENTITY_TYPE =
            "USER_ROLE";

    private static final String SOURCE_COMPONENT =
            "ADMINISTRATION";

    private final UserRoleRepository userRoleRepository;

    private final UserAccountRepository userAccountRepository;

    private final RoleRepository roleRepository;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final AuditEventServiceInterface auditEventService;

    public UserRoleAssignmentService(
            UserRoleRepository userRoleRepository,
            UserAccountRepository userAccountRepository,
            RoleRepository roleRepository,
            UserAccountLookupServiceInterface userAccountLookupService,
            AuditEventServiceInterface auditEventService) {

        this.userRoleRepository =
                userRoleRepository;

        this.userAccountRepository =
                userAccountRepository;

        this.roleRepository =
                roleRepository;

        this.userAccountLookupService =
                userAccountLookupService;

        this.auditEventService =
                auditEventService;
    }

    @Transactional
    public UserRole assignRole(
            UUID targetUserId,
            UUID roleId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                null;

        try {

            requirePermission(
                    securityContext,
                    PERMISSION_MANAGE
            );

            actor =
                    resolveActor(
                            securityContext
                    );

            requireIdentifier(
                    targetUserId,
                    "targetUserId"
            );

            requireIdentifier(
                    roleId,
                    "roleId"
            );

            UserAccount targetUser =
                    requireTargetUser(
                            targetUserId,
                            actor
                    );

            Role role =
                    requireAssignableRole(
                            roleId,
                            actor
                    );

            LocalDateTime now =
                    LocalDateTime.now();

            if (
                userRoleRepository
                        .findActiveAssignment(
                                targetUser.getUserId(),
                                role.getRoleId(),
                                now
                        )
                        .isPresent()
            ) {

                throw new DuplicateRecordException(
                        "Active user role assignment already exists."
                );
            }

            UserRole assignment =
                    new UserRole();

            assignment.setUserId(
                    targetUser.getUserId()
            );

            assignment.setRoleId(
                    role.getRoleId()
            );

            assignment.setEffectiveFrom(
                    now
            );

            assignment.setEffectiveTo(
                    null
            );

            assignment.setAssignedBy(
                    actor.userId()
            );

            assignment.setAssignedAt(
                    now
            );

            UserRole saved =
                    userRoleRepository.save(
                            assignment
                    );

            audit(
                    securityContext,
                    actor,
                    saved.getUserRoleId(),
                    "ASSIGN",
                    "SUCCESS",
                    details(
                            targetUserId,
                            roleId,
                            null,
                            null
                    )
            );

            return saved;
        }
        catch (
            AccessDeniedException
            | ResourceNotFoundException
            | DuplicateRecordException
            | IllegalArgumentException exception
        ) {

            audit(
                    securityContext,
                    actor,
                    null,
                    "ASSIGN",
                    "REJECTED",
                    details(
                            targetUserId,
                            roleId,
                            reasonFor(exception),
                            null
                    )
            );

            throw exception;
        }
        catch (RuntimeException exception) {

            audit(
                    securityContext,
                    actor,
                    null,
                    "ASSIGN",
                    "FAILURE",
                    details(
                            targetUserId,
                            roleId,
                            null,
                            exception
                                    .getClass()
                                    .getSimpleName()
                    )
            );

            throw exception;
        }
    }

    @Transactional
    public UserRole removeRole(
            UUID targetUserId,
            UUID roleId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                null;

        try {

            requirePermission(
                    securityContext,
                    PERMISSION_MANAGE
            );

            actor =
                    resolveActor(
                            securityContext
                    );

            requireIdentifier(
                    targetUserId,
                    "targetUserId"
            );

            requireIdentifier(
                    roleId,
                    "roleId"
            );

            requireTargetUser(
                    targetUserId,
                    actor
            );

            requireMutableRole(
                    roleId,
                    actor
            );

            LocalDateTime now =
                    LocalDateTime.now();

            UserRole assignment =
                    userRoleRepository
                            .findActiveAssignment(
                                    targetUserId,
                                    roleId,
                                    now
                            )
                            .orElseThrow(
                                    () ->
                                            new ResourceNotFoundException(
                                                    "Active user role assignment not found."
                                            )
                            );

            assignment.setEffectiveTo(
                    now
            );

            UserRole saved =
                    userRoleRepository.save(
                            assignment
                    );

            audit(
                    securityContext,
                    actor,
                    saved.getUserRoleId(),
                    "REMOVE",
                    "SUCCESS",
                    details(
                            targetUserId,
                            roleId,
                            null,
                            null
                    )
            );

            return saved;
        }
        catch (
            AccessDeniedException
            | ResourceNotFoundException
            | IllegalArgumentException exception
        ) {

            audit(
                    securityContext,
                    actor,
                    null,
                    "REMOVE",
                    "REJECTED",
                    details(
                            targetUserId,
                            roleId,
                            reasonFor(exception),
                            null
                    )
            );

            throw exception;
        }
        catch (RuntimeException exception) {

            audit(
                    securityContext,
                    actor,
                    null,
                    "REMOVE",
                    "FAILURE",
                    details(
                            targetUserId,
                            roleId,
                            null,
                            exception
                                    .getClass()
                                    .getSimpleName()
                    )
            );

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<UserRole> getUserRoles(
            UUID targetUserId,
            SecurityContext securityContext) {

        UserAccountReference actor =
                null;

        try {

            requirePermission(
                    securityContext,
                    PERMISSION_VIEW
            );

            actor =
                    resolveActor(
                            securityContext
                    );

            requireIdentifier(
                    targetUserId,
                    "targetUserId"
            );

            requireTargetUser(
                    targetUserId,
                    actor
            );

            LocalDateTime now =
                    LocalDateTime.now();

            List<UserRole> assignments =
                    userRoleRepository
                            .findActiveByUserId(
                                    targetUserId,
                                    now
                            );

            audit(
                    securityContext,
                    actor,
                    null,
                    "VIEW",
                    "SUCCESS",
                    details(
                            targetUserId,
                            null,
                            null,
                            null,
                            assignments.size()
                    )
            );

            return assignments;
        }
        catch (
            AccessDeniedException
            | ResourceNotFoundException
            | IllegalArgumentException exception
        ) {

            audit(
                    securityContext,
                    actor,
                    null,
                    "VIEW",
                    "REJECTED",
                    details(
                            targetUserId,
                            null,
                            reasonFor(exception),
                            null
                    )
            );

            throw exception;
        }
        catch (RuntimeException exception) {

            audit(
                    securityContext,
                    actor,
                    null,
                    "VIEW",
                    "FAILURE",
                    details(
                            targetUserId,
                            null,
                            null,
                            exception
                                    .getClass()
                                    .getSimpleName()
                    )
            );

            throw exception;
        }
    }

    private UserAccountReference resolveActor(
            SecurityContext securityContext) {

        if (
            securityContext == null
            || securityContext.getUserId() == null
        ) {

            throw new AccessDeniedException(
                    "Authenticated actor is required."
            );
        }

        UserAccountReference actor =
                userAccountLookupService
                        .getAuthorizedUser(
                                securityContext.getUserId()
                        );

        if (
            actor == null
            || actor.organizationId() == null
        ) {

            throw new AccessDeniedException(
                    "Authorized actor organization is required."
            );
        }

        return actor;
    }

    private UserAccount requireTargetUser(
            UUID targetUserId,
            UserAccountReference actor) {

        UserAccount targetUser =
                userAccountRepository
                        .findById(
                                targetUserId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User account not found."
                                        )
                        );

        if (
            targetUser.getOrganizationId() == null
            || !actor.organizationId()
                    .equals(
                            targetUser.getOrganizationId()
                    )
        ) {

            throw new AccessDeniedException(
                    "User account is outside the authorized organization."
            );
        }

        return targetUser;
    }

    private Role requireAssignableRole(
            UUID roleId,
            UserAccountReference actor) {

        Role role =
                requireMutableRole(
                        roleId,
                        actor
                );

        if (
            !"ACTIVE".equals(
                    role.getStatus()
            )
        ) {

            throw new IllegalArgumentException(
                    "Only ACTIVE roles may be assigned."
            );
        }

        return role;
    }

    private Role requireMutableRole(
            UUID roleId,
            UserAccountReference actor) {

        Role role =
                roleRepository
                        .findById(
                                roleId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Role not found."
                                        )
                        );

        if (
            Boolean.TRUE.equals(
                    role.getSystem()
            )
        ) {

            throw new AccessDeniedException(
                    "System role assignment is outside the institutional UC-008 flow."
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
                    "Role is outside the authorized organization."
            );
        }

        return role;
    }

    private void requirePermission(
            SecurityContext securityContext,
            String permissionCode) {

        if (
            securityContext == null
            || !securityContext.hasPermission(
                    permissionCode
            )
        ) {

            throw new AccessDeniedException(
                    "Missing required permission."
            );
        }
    }

    private void requireIdentifier(
            UUID value,
            String fieldName) {

        if (value == null) {

            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }
    }

    private String reasonFor(
            RuntimeException exception) {

        if (
            exception
                    instanceof AccessDeniedException
            && "Missing required permission."
                    .equals(
                            exception.getMessage()
                    )
        ) {

            return "MISSING_PERMISSION";
        }

        return exception
                .getClass()
                .getSimpleName();
    }

    private Map<String, Object> details(
            UUID targetUserId,
            UUID roleId,
            String reason,
            String exception) {

        return details(
                targetUserId,
                roleId,
                reason,
                exception,
                null
        );
    }

    private Map<String, Object> details(
            UUID targetUserId,
            UUID roleId,
            String reason,
            String exception,
            Integer resultCount) {

        Map<String, Object> details =
                new LinkedHashMap<>();

        if (targetUserId != null) {
            details.put(
                    "userId",
                    targetUserId
            );
        }

        if (roleId != null) {
            details.put(
                    "roleId",
                    roleId
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

    private void audit(
            SecurityContext securityContext,
            UserAccountReference actor,
            UUID entityId,
            String action,
            String result,
            Map<String, Object> details) {

        AuditEventRequest request =
                new AuditEventRequest();

        if (
            securityContext != null
        ) {

            request.setUserId(
                    securityContext.getUserId()
            );

            request.setSessionId(
                    securityContext.getSessionId()
            );
}

        if (actor != null) {

            request.setOrganizationId(
                    actor.organizationId()
            );
        }

        request.setEventType(
                EVENT_TYPE
        );

        request.setEntityType(
                ENTITY_TYPE
        );

        request.setEntityId(
                entityId
        );

        request.setAction(
                action
        );

        request.setSourceComponent(
                SOURCE_COMPONENT
        );

        request.setEventResult(
                result
        );

        request.setEventDetails(
                details
        );

        auditEventService
                .createAuditEventRequiresNew(
                        request
                );
    }
}