package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserSession;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.modules.administration.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserSessionBindingService {

    private static final String ACTIVE_SESSION_STATUS =
            "ACTIVE";

    private final UserSessionRepository
            userSessionRepository;

    private final AuditEventServiceInterface
            auditEventService;

    private final UserAccountLookupServiceInterface
            userAccountLookupService;

    public UserSessionBindingService(
            UserSessionRepository userSessionRepository,
            AuditEventServiceInterface auditEventService,
            UserAccountLookupServiceInterface userAccountLookupService) {

        this.userSessionRepository =
                userSessionRepository;

        this.auditEventService =
                auditEventService;

        this.userAccountLookupService =
                userAccountLookupService;
    }

    @Transactional
    public void establishSession(
            UUID sessionId,
            UUID userId) {

        validateIdentifiers(
                sessionId,
                userId
        );

        Optional<UserSession> existingSession =
                userSessionRepository.findById(
                        sessionId
                );

        if (existingSession.isPresent()) {

            UserSession session =
                    existingSession.get();

            if (!userId.equals(
                    session.getUserId()
            )) {

                throw new IllegalStateException(
                        "EFS session is bound to a different user: "
                                + sessionId
                );
            }

            if (!ACTIVE_SESSION_STATUS.equals(
                    session.getSessionStatus()
            )) {

                throw new IllegalStateException(
                        "EFS session cannot be reactivated: "
                                + sessionId
                );
            }

            return;
        }

        LocalDateTime now =
                LocalDateTime.now();

        UserSession session =
                new UserSession(
                        sessionId,
                        userId,
                        now,
                        ACTIVE_SESSION_STATUS,
                        now
                );

        userSessionRepository.saveAndFlush(
                session
        );
    }

    @Transactional
    public void invalidateSession(
            UUID sessionId,
            UUID userId) {

        validateIdentifiers(
                sessionId,
                userId
        );

        UserSession session =
                userSessionRepository
                        .findById(sessionId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "EFS session is not available: "
                                                + sessionId
                                )
                        );

        if (!userId.equals(
                session.getUserId()
        )) {

            throw new IllegalStateException(
                    "EFS session is bound to a different user: "
                            + sessionId
            );
        }

        if (!ACTIVE_SESSION_STATUS.equals(
                session.getSessionStatus()
        )) {

            throw new IllegalStateException(
                    "EFS session cannot be invalidated from status "
                            + session.getSessionStatus()
                            + ": "
                            + sessionId
            );
        }

        session.invalidate(
                LocalDateTime.now()
        );

        userSessionRepository.saveAndFlush(
                session
        );
    }

    @Transactional
    public void logout(
            SecurityContext securityContext) {

        if (securityContext == null) {
            throw new IllegalArgumentException(
                    "Security context is required."
            );
        }

        UUID userId =
                securityContext.getUserId();

        UUID sessionId =
                securityContext.getSessionId();

        validateIdentifiers(
                sessionId,
                userId
        );

        UserAccountReference authorizedUser =
                userAccountLookupService.getAuthorizedUser(
                        userId
                );

        try {
            invalidateSession(
                    sessionId,
                    userId
            );

        } catch (IllegalArgumentException | IllegalStateException exception) {

            auditLogoutRejected(
                    securityContext,
                    authorizedUser,
                    exception
            );

            throw exception;

        } catch (RuntimeException exception) {

            auditLogoutFailure(
                    securityContext,
                    authorizedUser,
                    exception
            );

            throw exception;
        }

        try {
            auditEventService.createAuditEvent(
                    buildLogoutAuditRequest(
                            securityContext,
                            authorizedUser,
                            "SUCCESS",
                            null
                    )
            );

        } catch (RuntimeException exception) {

            auditLogoutFailure(
                    securityContext,
                    authorizedUser,
                    exception
            );

            throw exception;
        }
    }

    private void auditLogoutRejected(
            SecurityContext securityContext,
            UserAccountReference authorizedUser,
            RuntimeException exception) {

        try {
            auditEventService.createAuditEventRequiresNew(
                    buildLogoutAuditRequest(
                            securityContext,
                            authorizedUser,
                            "REJECTED",
                            exception.getMessage()
                    )
            );
        } catch (RuntimeException auditException) {
            exception.addSuppressed(
                    auditException
            );
        }
    }
    private void auditLogoutFailure(
            SecurityContext securityContext,
            UserAccountReference authorizedUser,
            RuntimeException exception) {

        try {
            auditEventService.createAuditEventRequiresNew(
                    buildLogoutAuditRequest(
                            securityContext,
                            authorizedUser,
                            "FAILURE",
                            exception.getMessage()
                    )
            );
        } catch (RuntimeException auditException) {
            exception.addSuppressed(
                    auditException
            );
        }
    }

    private AuditEventRequest buildLogoutAuditRequest(
            SecurityContext securityContext,
            UserAccountReference authorizedUser,
            String result,
            String reason) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setOrganizationId(
                authorizedUser.organizationId()
        );

        request.setTenantId(
                authorizedUser.tenantId()
        );

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                "USER_LOGOUT"
        );

        request.setEntityType(
                "USER_SESSION"
        );

        request.setEntityId(
                securityContext.getSessionId()
        );

        request.setAction(
                "LOGOUT"
        );

        request.setSourceComponent(
                "ADMINISTRATION"
        );

        request.setEventResult(
                result
        );

        if (reason != null) {
            request.setEventDetails(
                    java.util.Map.of(
                            "reason",
                            reason
                    )
            );
        }

        return request;
    }
    public void requireActiveSession(
            UUID sessionId,
            UUID userId) {

        validateIdentifiers(
                sessionId,
                userId
        );

        if (userSessionRepository
                .findBySessionIdAndUserIdAndSessionStatus(
                        sessionId,
                        userId,
                        ACTIVE_SESSION_STATUS
                )
                .isEmpty()) {

            throw new IllegalStateException(
                    "Active EFS user session is not available: "
                            + sessionId
            );
        }
    }

    private void validateIdentifiers(
            UUID sessionId,
            UUID userId) {

        if (sessionId == null) {
            throw new IllegalStateException(
                    "Authenticated EFS sessionId is required"
            );
        }

        if (userId == null) {
            throw new IllegalArgumentException(
                    "userId is required"
            );
        }
    }
}
