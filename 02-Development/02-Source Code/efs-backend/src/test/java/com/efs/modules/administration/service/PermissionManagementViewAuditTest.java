package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Permission;
import com.efs.modules.administration.repository.PermissionRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PermissionManagementViewAuditTest {

    @Test
    void viewByIdSuccessShouldAuditExactlyOnce() {

        Fixture fixture =
                fixture("permission.view");

        UUID permissionId =
                UUID.randomUUID();

        when(
                fixture.repository.findById(
                        permissionId
                )
        ).thenReturn(
                Optional.of(
                        permission(
                                permissionId
                        )
                )
        );

        fixture.service.getPermission(
                permissionId,
                fixture.securityContext
        );

        AuditEventRequest request =
                captureAudit(
                        fixture.auditService
                );

        assertAudit(
                request,
                permissionId,
                "SUCCESS"
        );

        assertEquals(
                fixture.organizationId,
                request.getOrganizationId()
        );

        assertEquals(
                fixture.actorUserId,
                request.getUserId()
        );

        assertEquals(
                fixture.sessionId,
                request.getSessionId()
        );
    }

    @Test
    void viewCatalogSuccessShouldAuditResultCount() {

        Fixture fixture =
                fixture("permission.view");

        when(
                fixture.repository.findAll()
        ).thenReturn(
                List.of(
                        permission(
                                UUID.randomUUID()
                        ),
                        permission(
                                UUID.randomUUID()
                        )
                )
        );

        fixture.service.getPermissions(
                fixture.securityContext
        );

        AuditEventRequest request =
                captureAudit(
                        fixture.auditService
                );

        assertAudit(
                request,
                null,
                "SUCCESS"
        );

        assertEquals(
                2,
                request.getEventDetails().get(
                        "resultCount"
                )
        );
    }

    @Test
    void viewByIdMissingPermissionShouldAuditRejectedWithReason() {

        Fixture fixture =
                fixture("permission.manage");

        UUID permissionId =
                UUID.randomUUID();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.getPermission(
                                permissionId,
                                fixture.securityContext
                        )
        );

        AuditEventRequest request =
                captureAudit(
                        fixture.auditService
                );

        assertAudit(
                request,
                permissionId,
                "REJECTED"
        );

        assertEquals(
                "MISSING_PERMISSION",
                request.getEventDetails().get(
                        "reason"
                )
        );
    }

    @Test
    void viewCatalogMissingPermissionShouldAuditRejectedWithReason() {

        Fixture fixture =
                fixture("permission.manage");

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.getPermissions(
                                fixture.securityContext
                        )
        );

        AuditEventRequest request =
                captureAudit(
                        fixture.auditService
                );

        assertAudit(
                request,
                null,
                "REJECTED"
        );

        assertEquals(
                "MISSING_PERMISSION",
                request.getEventDetails().get(
                        "reason"
                )
        );
    }

    @Test
    void viewByIdUnknownPermissionShouldAuditRejected() {

        Fixture fixture =
                fixture("permission.view");

        UUID permissionId =
                UUID.randomUUID();

        when(
                fixture.repository.findById(
                        permissionId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        fixture.service.getPermission(
                                permissionId,
                                fixture.securityContext
                        )
        );

        AuditEventRequest request =
                captureAudit(
                        fixture.auditService
                );

        assertAudit(
                request,
                permissionId,
                "REJECTED"
        );

        assertEquals(
                "ResourceNotFoundException",
                request.getEventDetails().get(
                        "exception"
                )
        );
    }

    @Test
    void viewCatalogRepositoryFailureShouldAuditFailure() {

        Fixture fixture =
                fixture("permission.view");

        when(
                fixture.repository.findAll()
        ).thenThrow(
                new IllegalStateException(
                        "forced failure"
                )
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        fixture.service.getPermissions(
                                fixture.securityContext
                        )
        );

        AuditEventRequest request =
                captureAudit(
                        fixture.auditService
                );

        assertAudit(
                request,
                null,
                "FAILURE"
        );

        assertEquals(
                "IllegalStateException",
                request.getEventDetails().get(
                        "exception"
                )
        );
    }

    private static Permission permission(
            UUID permissionId) {

        Permission permission =
                new Permission();

        permission.setPermissionId(
                permissionId
        );

        permission.setPermissionCode(
                "case.review"
        );

        permission.setPermissionName(
                "Review Case"
        );

        permission.setResource(
                "case"
        );

        permission.setAction(
                "review"
        );

        return permission;
    }

    private static AuditEventRequest captureAudit(
            AuditEventServiceInterface auditService) {

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditService,
                times(1)
        ).createAuditEventRequiresNew(
                captor.capture()
        );

        return captor.getValue();
    }

    private static void assertAudit(
            AuditEventRequest request,
            UUID permissionId,
            String result) {

        assertEquals(
                "PERMISSION_MANAGEMENT",
                request.getEventType()
        );

        assertEquals(
                "PERMISSION",
                request.getEntityType()
        );

        if (permissionId == null) {
            assertNull(
                    request.getEntityId()
            );
        }
        else {
            assertEquals(
                    permissionId,
                    request.getEntityId()
            );
        }

        assertEquals(
                "VIEW",
                request.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                request.getSourceComponent()
        );

        assertEquals(
                result,
                request.getEventResult()
        );
    }

    private static Fixture fixture(
            String... permissions) {

        PermissionRepository repository =
                mock(
                        PermissionRepository.class
                );

        UserAccountLookupServiceInterface lookupService =
                mock(
                        UserAccountLookupServiceInterface.class
                );

        AuditEventServiceInterface auditService =
                mock(
                        AuditEventServiceInterface.class
                );

        UUID actorUserId =
                UUID.randomUUID();

        UUID organizationId =
                UUID.randomUUID();

        UUID sessionId =
                UUID.randomUUID();

        SecurityContext securityContext =
                new SecurityContext(
                        actorUserId,
                        null,
                        sessionId,
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

        PermissionManagementService service =
                new PermissionManagementService(
                        repository,
                        lookupService,
                        auditService
                );

        return new Fixture(
                service,
                repository,
                auditService,
                securityContext,
                actorUserId,
                organizationId,
                sessionId
        );
    }

    private record Fixture(
            PermissionManagementService service,
            PermissionRepository repository,
            AuditEventServiceInterface auditService,
            SecurityContext securityContext,
            UUID actorUserId,
            UUID organizationId,
            UUID sessionId) {
    }
}