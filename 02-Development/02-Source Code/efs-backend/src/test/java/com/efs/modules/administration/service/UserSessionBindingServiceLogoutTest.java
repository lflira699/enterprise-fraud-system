package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserSession;
import com.efs.modules.administration.repository.UserSessionRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSessionBindingServiceLogoutTest {

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private AuditEventServiceInterface auditEventService;

    @Mock
    private UserAccountLookupServiceInterface userAccountLookupService;

    private UserSessionBindingService service;

    private UUID userId;
    private UUID sessionId;
    private UUID organizationId;
    private UUID tenantId;

    private SecurityContext securityContext;
    private UserAccountReference authorizedUser;

    @BeforeEach
    void setUp() {

        service = new UserSessionBindingService(
                userSessionRepository,
                auditEventService,
                userAccountLookupService
        );

        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        organizationId = UUID.randomUUID();
        tenantId = UUID.randomUUID();

        securityContext = new SecurityContext(
                userId,
                tenantId,
                sessionId,
                Set.of("USER"),
                Set.of(),
                Set.of()
        );

        authorizedUser = new UserAccountReference(
                userId,
                organizationId,
                tenantId,
                "user@example.com"
        );
    }

    @Test
    void logoutShouldInvalidateActiveSessionAndAuditSuccess() {

        UserSession session = activeSession();

        when(userAccountLookupService.getAuthorizedUser(userId))
                .thenReturn(authorizedUser);

        when(userSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        service.logout(securityContext);

        verify(userSessionRepository)
                .saveAndFlush(session);

        assertEquals(
                "INVALIDATED",
                session.getSessionStatus()
        );

        assertNotNull(
                session.getLogoutTime()
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(auditEventService)
                .createAuditEvent(
                        captor.capture()
                );

        AuditEventRequest request =
                captor.getValue();

        assertEquals(
                organizationId,
                request.getOrganizationId()
        );

        assertEquals(
                tenantId,
                request.getTenantId()
        );

        assertEquals(
                userId,
                request.getUserId()
        );

        assertEquals(
                sessionId,
                request.getSessionId()
        );

        assertEquals(
                "USER_LOGOUT",
                request.getEventType()
        );

        assertEquals(
                "USER_SESSION",
                request.getEntityType()
        );

        assertEquals(
                sessionId,
                request.getEntityId()
        );

        assertEquals(
                "LOGOUT",
                request.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                request.getSourceComponent()
        );

        assertEquals(
                "SUCCESS",
                request.getEventResult()
        );

        verify(
                auditEventService,
                never()
        ).createAuditEventRequiresNew(
                any(AuditEventRequest.class)
        );
    }

    @Test
    void logoutShouldAuditRejectedWhenSessionDoesNotExist() {

        when(userAccountLookupService.getAuthorizedUser(userId))
                .thenReturn(authorizedUser);

        when(userSessionRepository.findById(sessionId))
                .thenReturn(Optional.empty());

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.logout(
                                securityContext
                        )
                );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(auditEventService)
                .createAuditEventRequiresNew(
                        captor.capture()
                );

        assertRejectedAudit(
                captor.getValue()
        );

        verify(
                auditEventService,
                never()
        ).createAuditEvent(
                any(AuditEventRequest.class)
        );

        assertNotNull(
                exception.getMessage()
        );
    }

    @Test
    void logoutShouldAuditRejectedWhenSessionBelongsToDifferentUser() {

        UUID differentUserId =
                UUID.randomUUID();

        UserSession session =
                new UserSession(
                        sessionId,
                        differentUserId,
                        LocalDateTime.now(),
                        "ACTIVE",
                        LocalDateTime.now()
                );

        when(userAccountLookupService.getAuthorizedUser(userId))
                .thenReturn(authorizedUser);

        when(userSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        assertThrows(
                IllegalStateException.class,
                () -> service.logout(
                        securityContext
                )
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(auditEventService)
                .createAuditEventRequiresNew(
                        captor.capture()
                );

        assertRejectedAudit(
                captor.getValue()
        );
    }

    @Test
    void logoutShouldAuditRejectedWhenSessionIsAlreadyInvalidated() {

        UserSession session =
                new UserSession(
                        sessionId,
                        userId,
                        LocalDateTime.now(),
                        "INVALIDATED",
                        LocalDateTime.now()
                );

        when(userAccountLookupService.getAuthorizedUser(userId))
                .thenReturn(authorizedUser);

        when(userSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        assertThrows(
                IllegalStateException.class,
                () -> service.logout(
                        securityContext
                )
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(auditEventService)
                .createAuditEventRequiresNew(
                        captor.capture()
                );

        assertRejectedAudit(
                captor.getValue()
        );
    }

    @Test
    void logoutShouldAuditRejectedWhenSessionIsExpired() {

        UserSession session =
                new UserSession(
                        sessionId,
                        userId,
                        LocalDateTime.now(),
                        "EXPIRED",
                        LocalDateTime.now()
                );

        when(userAccountLookupService.getAuthorizedUser(userId))
                .thenReturn(authorizedUser);

        when(userSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        assertThrows(
                IllegalStateException.class,
                () -> service.logout(
                        securityContext
                )
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(auditEventService)
                .createAuditEventRequiresNew(
                        captor.capture()
                );

        assertRejectedAudit(
                captor.getValue()
        );
    }

    @Test
    void logoutShouldAuditFailureForUnexpectedSessionPersistenceFailure() {

        UserSession session =
                activeSession();

        RuntimeException failure =
                new RuntimeException(
                        "persistence failure"
                );

        when(userAccountLookupService.getAuthorizedUser(userId))
                .thenReturn(authorizedUser);

        when(userSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        doThrow(failure)
                .when(userSessionRepository)
                .saveAndFlush(session);

        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class,
                        () -> service.logout(
                                securityContext
                        )
                );

        assertSame(
                failure,
                thrown
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(auditEventService)
                .createAuditEventRequiresNew(
                        captor.capture()
                );

        AuditEventRequest request =
                captor.getValue();

        assertEquals(
                "FAILURE",
                request.getEventResult()
        );

        assertEquals(
                "USER_LOGOUT",
                request.getEventType()
        );

        assertEquals(
                "LOGOUT",
                request.getAction()
        );

        assertEquals(
                "USER_SESSION",
                request.getEntityType()
        );
    }

    @Test
    void logoutShouldPropagateSuccessAuditFailureAndAuditFailureIndependently() {

        UserSession session =
                activeSession();

        RuntimeException auditFailure =
                new RuntimeException(
                        "audit failure"
                );

        when(userAccountLookupService.getAuthorizedUser(userId))
                .thenReturn(authorizedUser);

        when(userSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        doThrow(auditFailure)
                .when(auditEventService)
                .createAuditEvent(
                        any(AuditEventRequest.class)
                );

        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class,
                        () -> service.logout(
                                securityContext
                        )
                );

        assertSame(
                auditFailure,
                thrown
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(auditEventService)
                .createAuditEventRequiresNew(
                        captor.capture()
                );

        assertEquals(
                "FAILURE",
                captor.getValue().getEventResult()
        );
    }

    private UserSession activeSession() {

        return new UserSession(
                sessionId,
                userId,
                LocalDateTime.now(),
                "ACTIVE",
                LocalDateTime.now()
        );
    }

    private void assertRejectedAudit(
            AuditEventRequest request) {

        assertEquals(
                organizationId,
                request.getOrganizationId()
        );

        assertEquals(
                tenantId,
                request.getTenantId()
        );

        assertEquals(
                userId,
                request.getUserId()
        );

        assertEquals(
                sessionId,
                request.getSessionId()
        );

        assertEquals(
                "USER_LOGOUT",
                request.getEventType()
        );

        assertEquals(
                "USER_SESSION",
                request.getEntityType()
        );

        assertEquals(
                sessionId,
                request.getEntityId()
        );

        assertEquals(
                "LOGOUT",
                request.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                request.getSourceComponent()
        );

        assertEquals(
                "REJECTED",
                request.getEventResult()
        );

        assertNotNull(
                request.getEventDetails()
        );
    }
}