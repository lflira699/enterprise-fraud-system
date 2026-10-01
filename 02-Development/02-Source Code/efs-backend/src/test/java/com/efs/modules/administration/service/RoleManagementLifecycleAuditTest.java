package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Role;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

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

class RoleManagementLifecycleAuditTest {

    @Test
    void enableSuccessShouldAuditRoleManagementEnableSuccess() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role role =
                role(
                        roleId,
                        fixture.organizationId,
                        "INACTIVE"
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(role)
        );

        when(
                fixture.repository.save(
                        role
                )
        ).thenReturn(
                role
        );

        fixture.service.enableRole(
                roleId,
                fixture.securityContext
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditEventService
                );

        assertBaseAudit(
                audit,
                roleId,
                "ENABLE",
                "SUCCESS"
        );

        assertEquals(
                fixture.organizationId,
                audit.getOrganizationId()
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
    void disableSuccessShouldAuditRoleManagementDisableSuccess() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role role =
                role(
                        roleId,
                        fixture.organizationId,
                        "ACTIVE"
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(role)
        );

        when(
                fixture.repository.save(
                        role
                )
        ).thenReturn(
                role
        );

        fixture.service.disableRole(
                roleId,
                fixture.securityContext
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditEventService
                );

        assertBaseAudit(
                audit,
                roleId,
                "DISABLE",
                "SUCCESS"
        );

        assertEquals(
                fixture.organizationId,
                audit.getOrganizationId()
        );
    }

    @Test
    void enableMissingPermissionShouldAuditRejectedWithMissingPermissionReason() {

        Fixture fixture =
                fixture();

        UUID roleId =
                UUID.randomUUID();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.enableRole(
                                roleId,
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
                "ENABLE",
                "REJECTED"
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

        assertNull(
                audit.getOrganizationId()
        );

        verify(
                fixture.lookupService,
                never()
        ).getAuthorizedUser(
                any(UUID.class)
        );
    }

    @Test
    void disableMissingPermissionShouldAuditRejectedWithMissingPermissionReason() {

        Fixture fixture =
                fixture();

        UUID roleId =
                UUID.randomUUID();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.disableRole(
                                roleId,
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
                "DISABLE",
                "REJECTED"
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
    }

    @Test
    void enableMissingRoleShouldAuditRejected() {

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
                        fixture.service.enableRole(
                                roleId,
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
                "ENABLE",
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
    void disableMissingRoleShouldAuditRejected() {

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
                        fixture.service.disableRole(
                                roleId,
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
                "DISABLE",
                "REJECTED"
        );

        assertEquals(
                "ResourceNotFoundException",
                audit.getEventDetails()
                        .get("errorType")
        );
    }

    @Test
    void enableOtherOrganizationRoleShouldAuditRejected() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role role =
                role(
                        roleId,
                        UUID.randomUUID(),
                        "INACTIVE"
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
                        fixture.service.enableRole(
                                roleId,
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
                "ENABLE",
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
    void disableOtherOrganizationRoleShouldAuditRejected() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role role =
                role(
                        roleId,
                        UUID.randomUUID(),
                        "ACTIVE"
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
                        fixture.service.disableRole(
                                roleId,
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
                "DISABLE",
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
    void enableUnexpectedPersistenceFailureShouldAuditFailureAndPropagate() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role role =
                role(
                        roleId,
                        fixture.organizationId,
                        "INACTIVE"
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(role)
        );

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
                                fixture.service.enableRole(
                                        roleId,
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
                "ENABLE",
                "FAILURE"
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
    }

    @Test
    void disableUnexpectedPersistenceFailureShouldAuditFailureAndPropagate() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role role =
                role(
                        roleId,
                        fixture.organizationId,
                        "ACTIVE"
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(role)
        );

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
                                fixture.service.disableRole(
                                        roleId,
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
                "DISABLE",
                "FAILURE"
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
    }

    private static void assertBaseAudit(
            AuditEventRequest audit,
            UUID roleId,
            String action,
            String result) {

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
                action,
                audit.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                audit.getSourceComponent()
        );

        assertEquals(
                result,
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

    private static Role role(
            UUID roleId,
            UUID organizationId,
            String status) {

        Role role =
                new Role();

        role.setRoleId(
                roleId
        );

        role.setOrganizationId(
                organizationId
        );

        role.setRoleCode(
                "ROLE_CODE"
        );

        role.setRoleName(
                "Role Name"
        );

        role.setSystem(
                false
        );

        role.setStatus(
                status
        );

        return role;
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