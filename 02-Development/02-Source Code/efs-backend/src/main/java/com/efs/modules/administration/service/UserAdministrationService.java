package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserAccount;
import com.efs.modules.administration.repository.UserAccountRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserAdministrationService {

    private static final String PERMISSION_MANAGE =
            "user.manage";

    private static final String PERMISSION_VIEW =
            "user.view";
    private static final String EVENT_TYPE_USER_ADMINISTRATION =
            "USER_ADMINISTRATION";

    private static final String ENTITY_TYPE_USER_ACCOUNT =
            "USER_ACCOUNT";

    private static final String SOURCE_COMPONENT_ADMINISTRATION =
            "ADMINISTRATION";

    private static final String ACTION_CREATE =
            "CREATE";

    private static final String ACTION_UPDATE =
            "UPDATE";

    private static final String ACTION_ENABLE =
            "ENABLE";

    private static final String ACTION_DISABLE =
            "DISABLE";

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

    private final UserAccountRepository
            userAccountRepository;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    private final TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    private final AuditEventServiceInterface
            auditEventService;

    public UserAdministrationService(
            UserAccountRepository userAccountRepository,
            UserAccountLookupServiceInterface userAccountLookupService,
            TenantOrganizationLookupServiceInterface tenantOrganizationLookupService,
            AuditEventServiceInterface auditEventService) {

        this.userAccountRepository =
                userAccountRepository;

        this.userAccountLookupService =
                userAccountLookupService;

        this.tenantOrganizationLookupService =
                tenantOrganizationLookupService;

        this.auditEventService =
                auditEventService;
    }

    @Transactional
    public UserAccount createUser(
            UUID userId,
            UUID tenantId,
            UUID businessUnitId,
            String username,
            String fullName,
            String email,
            String authenticationProvider,
            boolean mfaEnabled,
            SecurityContext securityContext) {

        UserAccountReference actor = null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_MANAGE
                    );

            if (actor.tenantId() != null
                    && !actor.tenantId().equals(
                            tenantId
                    )) {
                throw new AccessDeniedException(
                        "Tenant-scoped user cannot create a user in another tenant"
                );
            }

            if (tenantId != null) {

                UUID tenantOrganizationId =
                        tenantOrganizationLookupService
                                .getOrganizationIdByTenantId(
                                        tenantId
                                );

                if (!actor.organizationId().equals(
                        tenantOrganizationId
                )) {
                    throw new AccessDeniedException(
                            "Tenant does not belong to the authorized organization"
                    );
                }
            }

            UserAccount userAccount =
                    new UserAccount();

            userAccount.setUserId(userId);
            userAccount.setOrganizationId(
                    actor.organizationId()
            );
            userAccount.setTenantId(tenantId);
            userAccount.setBusinessUnitId(
                    businessUnitId
            );
            userAccount.setUsername(username);
            userAccount.setFullName(fullName);
            userAccount.setEmail(email);
            userAccount.setAuthenticationProvider(
                    authenticationProvider
            );
            userAccount.setMfaEnabled(mfaEnabled);
            userAccount.setAccountStatus("ACTIVE");
            userAccount.setFailedLoginAttempts(0);

            UserAccount saved =
                    userAccountRepository.save(
                            userAccount
                    );

            recordAudit(
                    securityContext,
                    actor,
                    userId,
                    ACTION_CREATE,
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
                    userId,
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
                    userId,
                    ACTION_CREATE,
                    RESULT_FAILURE,
                    null,
                    exception
            );

            throw exception;
        }
    }

public UserAccount updateUser(
            UUID targetUserId,
            UUID tenantId,
            UUID businessUnitId,
            String username,
            String fullName,
            String email,
            String authenticationProvider,
            Boolean mfaEnabled,
            SecurityContext securityContext
    ) {

        UserAccountReference actor = null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_MANAGE
                    );

            UserAccount userAccount =
                    userAccountRepository.findById(targetUserId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User account not found: "
                                                    + targetUserId
                                    )
                            );

            if (!actor.organizationId().equals(
                    userAccount.getOrganizationId()
            )) {
                throw new AccessDeniedException(
                        "User account does not belong to the authorized organization"
                );
            }

            if (actor.tenantId() != null
                    && !actor.tenantId().equals(
                            userAccount.getTenantId()
                    )) {
                throw new AccessDeniedException(
                        "Tenant-scoped user cannot update another tenant"
                );
            }

            if (actor.tenantId() != null
                    && !actor.tenantId().equals(
                            tenantId
                    )) {
                throw new AccessDeniedException(
                        "Tenant-scoped user cannot move a user to another tenant"
                );
            }

            if (tenantId != null) {

                UUID tenantOrganizationId =
                        tenantOrganizationLookupService
                                .getOrganizationIdByTenantId(
                                        tenantId
                                );

                if (!actor.organizationId().equals(
                        tenantOrganizationId
                )) {
                    throw new AccessDeniedException(
                            "Tenant does not belong to the authorized organization"
                    );
                }
            }

            userAccount.setTenantId(tenantId);
            userAccount.setBusinessUnitId(businessUnitId);
            userAccount.setUsername(username);
            userAccount.setFullName(fullName);
            userAccount.setEmail(email);
            userAccount.setAuthenticationProvider(authenticationProvider);
            userAccount.setMfaEnabled(mfaEnabled);

            UserAccount saved =
                    userAccountRepository.save(
                            userAccount
                    );

            recordAudit(
                    securityContext,
                    actor,
                    targetUserId,
                    ACTION_UPDATE,
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
                    targetUserId,
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
                    targetUserId,
                    ACTION_UPDATE,
                    RESULT_FAILURE,
                    null,
                    exception
            );

            throw exception;
        }
    }

public UserAccount enableUser(
            UUID targetUserId,
            SecurityContext securityContext
    ) {

        UserAccountReference actor = null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_MANAGE
                    );

            UserAccount userAccount =
                    userAccountRepository.findById(targetUserId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User account not found: "
                                                    + targetUserId
                                    )
                            );

            if (!actor.organizationId().equals(
                    userAccount.getOrganizationId()
            )) {
                throw new AccessDeniedException(
                        "User account does not belong to the authorized organization"
                );
            }

            if (actor.tenantId() != null
                    && !actor.tenantId().equals(
                            userAccount.getTenantId()
                    )) {
                throw new AccessDeniedException(
                        "Tenant-scoped user cannot enable another tenant"
                );
            }

            userAccount.enable();

            UserAccount saved =
                    userAccountRepository.save(
                            userAccount
                    );

            recordAudit(
                    securityContext,
                    actor,
                    targetUserId,
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
                    targetUserId,
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
                    targetUserId,
                    ACTION_ENABLE,
                    RESULT_FAILURE,
                    null,
                    exception
            );

            throw exception;
        }
    }

public UserAccount disableUser(
            UUID targetUserId,
            SecurityContext securityContext
    ) {

        UserAccountReference actor = null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_MANAGE
                    );

            UserAccount userAccount =
                    userAccountRepository.findById(targetUserId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User account not found: "
                                                    + targetUserId
                                    )
                            );

            if (!actor.organizationId().equals(
                    userAccount.getOrganizationId()
            )) {
                throw new AccessDeniedException(
                        "User account does not belong to the authorized organization"
                );
            }

            if (actor.tenantId() != null
                    && !actor.tenantId().equals(
                            userAccount.getTenantId()
                    )) {
                throw new AccessDeniedException(
                        "Tenant-scoped user cannot disable another tenant"
                );
            }

            userAccount.disable();

            UserAccount saved =
                    userAccountRepository.save(
                            userAccount
                    );

            recordAudit(
                    securityContext,
                    actor,
                    targetUserId,
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
                    targetUserId,
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
                    targetUserId,
                    ACTION_DISABLE,
                    RESULT_FAILURE,
                    null,
                    exception
            );

            throw exception;
        }
    }

public UserAccount getUser(
            UUID targetUserId,
            SecurityContext securityContext
    ) {

        UserAccountReference actor = null;

        try {

            actor =
                    authorize(
                            securityContext,
                            PERMISSION_VIEW
                    );

            UserAccount userAccount =
                    userAccountRepository.findById(targetUserId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User account not found: "
                                                    + targetUserId
                                    )
                            );

            if (!actor.organizationId().equals(
                    userAccount.getOrganizationId()
            )) {
                throw new AccessDeniedException(
                        "User account does not belong to the authorized organization"
                );
            }

            if (actor.tenantId() != null
                    && !actor.tenantId().equals(
                            userAccount.getTenantId()
                    )) {
                throw new AccessDeniedException(
                        "Tenant-scoped user cannot consult another tenant"
                );
            }

            recordAudit(
                    securityContext,
                    actor,
                    targetUserId,
                    ACTION_VIEW,
                    RESULT_SUCCESS,
                    null,
                    null
            );

            return userAccount;

        } catch (
                ResourceNotFoundException
                        | AccessDeniedException exception
        ) {

            recordAudit(
                    securityContext,
                    actor,
                    targetUserId,
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
                    targetUserId,
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
                EVENT_TYPE_USER_ADMINISTRATION
        );

        request.setEntityType(
                ENTITY_TYPE_USER_ACCOUNT
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

        UserAccountReference authorizedUser =
                userAccountLookupService
                        .getAuthorizedUser(
                                userId
                        );

        if (
                authorizedUser == null
                        || authorizedUser.organizationId()
                        == null
        ) {
            throw new AccessDeniedException(
                    "Authorized organization is required"
            );
        }

        return authorizedUser;
    }
}
