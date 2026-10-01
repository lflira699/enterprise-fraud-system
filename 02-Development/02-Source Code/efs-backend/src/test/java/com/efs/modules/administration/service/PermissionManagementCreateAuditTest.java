package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Permission;
import com.efs.modules.administration.repository.PermissionRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PermissionManagementCreateAuditTest {

    @Test
    void createSuccessShouldAuditExactlyOnce() {

        Fixture fixture =
                fixture("permission.manage");

        UUID permissionId =
                UUID.randomUUID();

        when(
                fixture.repository.existsByPermissionCode(
                        "case.review"
                )
        ).thenReturn(false);

        when(
                fixture.repository.save(
                        any(Permission.class)
                )
        ).thenAnswer(invocation -> {
            Permission permission =
                    invocation.getArgument(0);

            permission.setPermissionId(
                    permissionId
            );

            return permission;
        });

        fixture.service.createPermission(
                "case.review",
                "Review Case",
                "case",
                "review",
                null,
                fixture.securityContext
        );

        AuditEventRequest request =
                captureAudit(
                        fixture.auditService
                );

        assertAuditBase(
                request,
                permissionId,
                "CREATE",
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
    void duplicateCreateShouldAuditRejected() {

        Fixture fixture =
                fixture("permission.manage");

        when(
                fixture.repository.existsByPermissionCode(
                        "case.review"
                )
        ).thenReturn(true);

        assertThrows(
                DuplicateRecordException.class,
                () ->
                        fixture.service.createPermission(
                                "case.review",
                                "Review Case",
                                "case",
                                "review",
                                null,
                                fixture.securityContext
                        )
        );

        AuditEventRequest request =
                captureAudit(
                        fixture.auditService
                );

        assertAuditBase(
                request,
                null,
                "CREATE",
                "REJECTED"
        );

        assertEquals(
                "DuplicateRecordException",
                request.getEventDetails().get(
                        "exception"
                )
        );
    }

    @Test
    void missingManagePermissionShouldAuditRejectedWithReason() {

        Fixture fixture =
                fixture("permission.view");

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.createPermission(
                                "case.review",
                                "Review Case",
                                "case",
                                "review",
                                null,
                                fixture.securityContext
                        )
        );

        AuditEventRequest request =
                captureAudit(
                        fixture.auditService
                );

        assertAuditBase(
                request,
                null,
                "CREATE",
                "REJECTED"
        );

        assertEquals(
                "MISSING_PERMISSION",
                request.getEventDetails().get(
                        "reason"
                )
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
    void repositoryFailureShouldAuditFailure() {

        Fixture fixture =
                fixture("permission.manage");

        when(
                fixture.repository.existsByPermissionCode(
                        "case.review"
                )
        ).thenReturn(false);

        when(
                fixture.repository.save(
                        any(Permission.class)
                )
        ).thenThrow(
                new IllegalStateException(
                        "forced failure"
                )
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        fixture.service.createPermission(
                                "case.review",
                                "Review Case",
                                "case",
                                "review",
                                null,
                                fixture.securityContext
                        )
        );

        AuditEventRequest request =
                captureAudit(
                        fixture.auditService
                );

        assertAuditBase(
                request,
                null,
                "CREATE",
                "FAILURE"
        );

        assertEquals(
                "IllegalStateException",
                request.getEventDetails().get(
                        "exception"
                )
        );
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

    private static void assertAuditBase(
            AuditEventRequest request,
            UUID entityId,
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

        if (entityId == null) {
            assertNull(
                    request.getEntityId()
            );
        }
        else {
            assertEquals(
                    entityId,
                    request.getEntityId()
            );
        }

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