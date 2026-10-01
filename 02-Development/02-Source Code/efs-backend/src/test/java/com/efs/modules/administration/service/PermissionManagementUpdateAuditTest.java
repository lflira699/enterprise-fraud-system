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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PermissionManagementUpdateAuditTest {

    @Test
    void updateSuccessShouldAuditExactlyOnce() {

        Fixture fixture =
                fixture("permission.manage");

        UUID permissionId =
                UUID.randomUUID();

        Permission permission =
                permission(
                        permissionId
                );

        when(
                fixture.repository.findById(
                        permissionId
                )
        ).thenReturn(
                Optional.of(
                        permission
                )
        );

        when(
                fixture.repository.save(
                        permission
                )
        ).thenReturn(
                permission
        );

        fixture.service.updatePermission(
                permissionId,
                "Updated Permission",
                "Updated description",
                fixture.securityContext
        );

        AuditEventRequest request =
                captureAudit(
                        fixture.auditService
                );

        assertAudit(
                request,
                permissionId,
                "UPDATE",
                "SUCCESS"
        );
    }

    @Test
    void unknownPermissionShouldAuditRejected() {

        Fixture fixture =
                fixture("permission.manage");

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
                        fixture.service.updatePermission(
                                permissionId,
                                "Updated Permission",
                                null,
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
                "UPDATE",
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
    void missingManagePermissionShouldAuditRejectedWithReason() {

        Fixture fixture =
                fixture("permission.view");

        UUID permissionId =
                UUID.randomUUID();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.updatePermission(
                                permissionId,
                                "Updated Permission",
                                null,
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
                "UPDATE",
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
    void repositoryFailureShouldAuditFailure() {

        Fixture fixture =
                fixture("permission.manage");

        UUID permissionId =
                UUID.randomUUID();

        when(
                fixture.repository.findById(
                        permissionId
                )
        ).thenThrow(
                new IllegalStateException(
                        "forced failure"
                )
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        fixture.service.updatePermission(
                                permissionId,
                                "Updated Permission",
                                null,
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
                "UPDATE",
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
            String action,
            String result) {

        assertEquals(
                "PERMISSION_MANAGEMENT",
                request.getEventType()
        );

        assertEquals(
                "PERMISSION",
                request.getEntityType()
        );

        assertEquals(
                permissionId,
                request.getEntityId()
        );

        assertEquals(
                action,
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
                securityContext
        );
    }

    private record Fixture(
            PermissionManagementService service,
            PermissionRepository repository,
            AuditEventServiceInterface auditService,
            SecurityContext securityContext) {
    }
}