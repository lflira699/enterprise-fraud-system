package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Role;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;
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

class RoleManagementUpdateAuditTest {

    @Test
    void updateSuccessShouldAuditRoleManagementUpdateSuccess() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role role =
                role(
                        roleId,
                        fixture.organizationId
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(role)
        );

        when(
                fixture.repository.existsByRoleCodeAndRoleIdNot(
                        "FRAUD_SUPERVISOR",
                        roleId
                )
        ).thenReturn(false);

        when(
                fixture.repository
                        .existsByRoleNameAndOrganizationIdAndRoleIdNot(
                                "Fraud Supervisor",
                                fixture.organizationId,
                                roleId
                        )
        ).thenReturn(false);

        when(
                fixture.repository.save(
                        role
                )
        ).thenReturn(
                role
        );

        fixture.service.updateRole(
                roleId,
                "FRAUD_SUPERVISOR",
                "Fraud Supervisor",
                "Updated role",
                fixture.securityContext
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditEventService
                );

        assertBaseAudit(
                audit,
                roleId,
                "SUCCESS"
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

        UUID roleId =
                UUID.randomUUID();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.updateRole(
                                roleId,
                                "FRAUD_SUPERVISOR",
                                "Fraud Supervisor",
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
                roleId,
                "REJECTED"
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

        verify(
                fixture.lookupService,
                never()
        ).getAuthorizedUser(
                any(UUID.class)
        );
    }

    @Test
    void missingRoleShouldAuditRejected() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        fixture.service.updateRole(
                                roleId,
                                "FRAUD_SUPERVISOR",
                                "Fraud Supervisor",
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
                roleId,
                "REJECTED"
        );

        assertEquals(
                fixture.organizationId,
                audit.getOrganizationId()
        );

        assertEquals(
                "ResourceNotFoundException",
                audit.getEventDetails()
                        .get("errorType")
        );
    }

    @Test
    void otherOrganizationRoleShouldAuditRejected() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role role =
                role(
                        roleId,
                        UUID.randomUUID()
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(role)
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.updateRole(
                                roleId,
                                "FRAUD_SUPERVISOR",
                                "Fraud Supervisor",
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
                roleId,
                "REJECTED"
        );

        assertEquals(
                fixture.organizationId,
                audit.getOrganizationId()
        );

        assertEquals(
                "AccessDeniedException",
                audit.getEventDetails()
                        .get("errorType")
        );

        assertNull(
                audit.getEventDetails()
                        .get("reason")
        );
    }

    @Test
    void duplicateRoleCodeShouldAuditRejected() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role role =
                role(
                        roleId,
                        fixture.organizationId
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(role)
        );

        when(
                fixture.repository.existsByRoleCodeAndRoleIdNot(
                        "FRAUD_SUPERVISOR",
                        roleId
                )
        ).thenReturn(true);

        assertThrows(
                DuplicateRecordException.class,
                () ->
                        fixture.service.updateRole(
                                roleId,
                                "FRAUD_SUPERVISOR",
                                "Fraud Supervisor",
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
                roleId,
                "REJECTED"
        );

        assertEquals(
                "DuplicateRecordException",
                audit.getEventDetails()
                        .get("errorType")
        );

        assertEquals(
                "Role code already exists: FRAUD_SUPERVISOR",
                audit.getEventDetails()
                        .get("errorMessage")
        );
    }

    @Test
    void unexpectedPersistenceFailureShouldAuditFailureAndPropagate() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role role =
                role(
                        roleId,
                        fixture.organizationId
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(role)
        );

        when(
                fixture.repository.existsByRoleCodeAndRoleIdNot(
                        "FRAUD_SUPERVISOR",
                        roleId
                )
        ).thenReturn(false);

        when(
                fixture.repository
                        .existsByRoleNameAndOrganizationIdAndRoleIdNot(
                                "Fraud Supervisor",
                                fixture.organizationId,
                                roleId
                        )
        ).thenReturn(false);

        IllegalStateException failure =
                new IllegalStateException(
                        "persistence failure"
                );

        when(
                fixture.repository.save(
                        role
                )
        ).thenThrow(
                failure
        );

        IllegalStateException thrown =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                fixture.service.updateRole(
                                        roleId,
                                        "FRAUD_SUPERVISOR",
                                        "Fraud Supervisor",
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
                roleId,
                "FAILURE"
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
            UUID roleId,
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
                roleId,
                audit.getEntityId()
        );

        assertEquals(
                "UPDATE",
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

    private static Role role(
            UUID roleId,
            UUID organizationId) {

        Role role =
                new Role();

        role.setRoleId(
                roleId
        );

        role.setOrganizationId(
                organizationId
        );

        role.setRoleCode(
                "FRAUD_ANALYST"
        );

        role.setRoleName(
                "Fraud Analyst"
        );

        role.setDescription(
                "Existing role"
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

        return role;
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