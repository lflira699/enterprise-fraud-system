package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Role;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleManagementCreateAuditTest {

    @Test
    void createSuccessShouldAuditRoleManagementCreateSuccess() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        when(
                fixture.repository.existsByRoleCode(
                        "FRAUD_ANALYST"
                )
        ).thenReturn(false);

        when(
                fixture.repository
                        .existsByRoleNameAndOrganizationId(
                                "Fraud Analyst",
                                fixture.organizationId
                        )
        ).thenReturn(false);

        when(
                fixture.repository.save(
                        any(Role.class)
                )
        ).thenAnswer(
                invocation -> {
                    Role role =
                            invocation.getArgument(0);

                    role.setRoleId(
                            roleId
                    );

                    return role;
                }
        );

        fixture.service.createRole(
                "FRAUD_ANALYST",
                "Fraud Analyst",
                "Fraud investigation role",
                fixture.securityContext
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditEventService
                );

        assertBaseAudit(
                audit,
                "SUCCESS"
        );

        assertEquals(
                roleId,
                audit.getEntityId()
        );

        assertEquals(
                fixture.organizationId,
                audit.getOrganizationId()
        );

        assertNull(
                audit.getTenantId()
        );

        assertEquals(
                fixture.securityContext.getUserId(),
                audit.getUserId()
        );

        assertEquals(
                fixture.securityContext.getSessionId(),
                audit.getSessionId()
        );
    }

    @Test
    void missingManagePermissionShouldAuditRejectedWithMissingPermissionReason() {

        Fixture fixture =
                fixture();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.createRole(
                                "FRAUD_ANALYST",
                                "Fraud Analyst",
                                null,
                                fixture.securityContext
                        )
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditEventService
                );

        assertBaseAudit(
                audit,
                "REJECTED"
        );

        assertNull(
                audit.getEntityId()
        );

        assertNull(
                audit.getOrganizationId()
        );

        assertEquals(
                "MISSING_PERMISSION",
                audit.getEventDetails()
                        .get("reason")
        );

        assertEquals(
                "AccessDeniedException",
                audit.getEventDetails()
                        .get("errorType")
        );

        assertEquals(
                fixture.securityContext.getUserId(),
                audit.getUserId()
        );

        assertEquals(
                fixture.securityContext.getSessionId(),
                audit.getSessionId()
        );

        verify(
                fixture.lookupService,
                never()
        ).getAuthorizedUser(
                any(UUID.class)
        );
    }

    @Test
    void duplicateRoleCodeShouldAuditRejected() {

        Fixture fixture =
                fixture("role.manage");

        when(
                fixture.repository.existsByRoleCode(
                        "FRAUD_ANALYST"
                )
        ).thenReturn(true);

        assertThrows(
                DuplicateRecordException.class,
                () ->
                        fixture.service.createRole(
                                "FRAUD_ANALYST",
                                "Fraud Analyst",
                                null,
                                fixture.securityContext
                        )
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditEventService
                );

        assertBaseAudit(
                audit,
                "REJECTED"
        );

        assertNull(
                audit.getEntityId()
        );

        assertEquals(
                fixture.organizationId,
                audit.getOrganizationId()
        );

        assertEquals(
                "DuplicateRecordException",
                audit.getEventDetails()
                        .get("errorType")
        );

        assertEquals(
                "Role code already exists: FRAUD_ANALYST",
                audit.getEventDetails()
                        .get("errorMessage")
        );

        assertNull(
                audit.getEventDetails()
                        .get("reason")
        );
    }

    @Test
    void invalidRoleCodeShouldAuditRejected() {

        Fixture fixture =
                fixture("role.manage");

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        fixture.service.createRole(
                                " ",
                                "Fraud Analyst",
                                null,
                                fixture.securityContext
                        )
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditEventService
                );

        assertBaseAudit(
                audit,
                "REJECTED"
        );

        assertNull(
                audit.getEntityId()
        );

        assertEquals(
                fixture.organizationId,
                audit.getOrganizationId()
        );

        assertEquals(
                "IllegalArgumentException",
                audit.getEventDetails()
                        .get("errorType")
        );

        assertEquals(
                "Role code is required",
                audit.getEventDetails()
                        .get("errorMessage")
        );
    }

    @Test
    void unexpectedPersistenceFailureShouldAuditFailureAndPropagate() {

        Fixture fixture =
                fixture("role.manage");

        when(
                fixture.repository.existsByRoleCode(
                        "FRAUD_ANALYST"
                )
        ).thenReturn(false);

        when(
                fixture.repository
                        .existsByRoleNameAndOrganizationId(
                                "Fraud Analyst",
                                fixture.organizationId
                        )
        ).thenReturn(false);

        IllegalStateException failure =
                new IllegalStateException(
                        "persistence failure"
                );

        when(
                fixture.repository.save(
                        any(Role.class)
                )
        ).thenThrow(
                failure
        );

        IllegalStateException thrown =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                fixture.service.createRole(
                                        "FRAUD_ANALYST",
                                        "Fraud Analyst",
                                        null,
                                        fixture.securityContext
                                )
                );

        assertSame(
                failure,
                thrown
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditEventService
                );

        assertBaseAudit(
                audit,
                "FAILURE"
        );

        assertNull(
                audit.getEntityId()
        );

        assertEquals(
                fixture.organizationId,
                audit.getOrganizationId()
        );

        assertEquals(
                "IllegalStateException",
                audit.getEventDetails()
                        .get("errorType")
        );

        assertEquals(
                "persistence failure",
                audit.getEventDetails()
                        .get("errorMessage")
        );

        assertNull(
                audit.getEventDetails()
                        .get("reason")
        );
    }

    private static void assertBaseAudit(
            AuditEventRequest audit,
            String expectedResult) {

        assertEquals(
                "ROLE_MANAGEMENT",
                audit.getEventType()
        );

        assertEquals(
                "ROLE",
                audit.getEntityType()
        );

        assertEquals(
                "CREATE",
                audit.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                audit.getSourceComponent()
        );

        assertEquals(
                expectedResult,
                audit.getEventResult()
        );
    }

    private static AuditEventRequest captureAudit(
            AuditEventServiceInterface auditEventService) {

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEventRequiresNew(
                captor.capture()
        );

        return captor.getValue();
    }

    private static Fixture fixture(
            String... permissions) {

        RoleRepository repository =
                mock(
                        RoleRepository.class
                );

        UserAccountLookupServiceInterface lookupService =
                mock(
                        UserAccountLookupServiceInterface.class
                );

        AuditEventServiceInterface auditEventService =
                mock(
                        AuditEventServiceInterface.class
                );

        UUID actorUserId =
                UUID.randomUUID();

        UUID organizationId =
                UUID.randomUUID();

        SecurityContext securityContext =
                new SecurityContext(
                        actorUserId,
                        null,
                        UUID.randomUUID(),
                        Set.of(),
                        Set.of(permissions),
                        Set.of()
                );

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        null,
                        "actor@example.com"
                );

        when(
                lookupService.getAuthorizedUser(
                        actorUserId
                )
        ).thenReturn(
                actor
        );

        RoleManagementService service =
                new RoleManagementService(
                        repository,
                        lookupService,
                        auditEventService
                );

        return new Fixture(
                service,
                repository,
                lookupService,
                auditEventService,
                securityContext,
                organizationId
        );
    }

    private record Fixture(
            RoleManagementService service,
            RoleRepository repository,
            UserAccountLookupServiceInterface lookupService,
            AuditEventServiceInterface auditEventService,
            SecurityContext securityContext,
            UUID organizationId) {
    }
}