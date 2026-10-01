package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Permission;
import com.efs.modules.administration.repository.PermissionRepository;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PermissionManagementCreateTest {

    @Test
    void createShouldPersistPermissionWithManagePermission() {

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

        Permission result =
                fixture.service.createPermission(
                        "case.review",
                        "Review Case",
                        "case",
                        "review",
                        "Allows case review",
                        fixture.securityContext
                );

        assertEquals(
                permissionId,
                result.getPermissionId()
        );

        assertEquals(
                "case.review",
                result.getPermissionCode()
        );

        assertEquals(
                "Review Case",
                result.getPermissionName()
        );

        assertEquals(
                "case",
                result.getResource()
        );

        assertEquals(
                "review",
                result.getAction()
        );

        assertEquals(
                "Allows case review",
                result.getDescription()
        );

        verify(
                fixture.repository
        ).save(
                any(Permission.class)
        );
    }

    @Test
    void createShouldRejectDuplicatePermissionCode() {

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

        verify(
                fixture.repository,
                never()
        ).save(
                any(Permission.class)
        );
    }

    @Test
    void createShouldRejectMissingManagePermission() {

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

        verify(
                fixture.repository,
                never()
        ).existsByPermissionCode(
                any(String.class)
        );

        verify(
                fixture.repository,
                never()
        ).save(
                any(Permission.class)
        );
    }

    @Test
    void createShouldRejectBlankPermissionCode() {

        Fixture fixture =
                fixture("permission.manage");

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                fixture.service.createPermission(
                                        " ",
                                        "Review Case",
                                        "case",
                                        "review",
                                        null,
                                        fixture.securityContext
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                "Permission code is required"
                        )
        );

        verify(
                fixture.repository,
                never()
        ).save(
                any(Permission.class)
        );
    }

    @Test
    void createShouldRejectResourceExceedingMaximumLength() {

        Fixture fixture =
                fixture("permission.manage");

        String resource =
                "x".repeat(81);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                fixture.service.createPermission(
                                        "case.review",
                                        "Review Case",
                                        resource,
                                        "review",
                                        null,
                                        fixture.securityContext
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                "Resource exceeds maximum length 80"
                        )
        );

        verify(
                fixture.repository,
                never()
        ).save(
                any(Permission.class)
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
                securityContext
        );
    }

    private record Fixture(
            PermissionManagementService service,
            PermissionRepository repository,
            SecurityContext securityContext) {
    }
}