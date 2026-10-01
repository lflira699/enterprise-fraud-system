package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Permission;
import com.efs.modules.administration.repository.PermissionRepository;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PermissionManagementViewTest {

    @Test
    void viewByIdShouldReturnPermission() {

        Fixture fixture =
                fixture("permission.view");

        UUID permissionId =
                UUID.randomUUID();

        Permission permission =
                permission(
                        permissionId,
                        "case.review"
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

        Permission result =
                fixture.service.getPermission(
                        permissionId,
                        fixture.securityContext
                );

        assertSame(
                permission,
                result
        );

        verify(
                fixture.repository,
                never()
        ).save(
                any(Permission.class)
        );
    }

    @Test
    void viewCatalogShouldReturnAllPermissions() {

        Fixture fixture =
                fixture("permission.view");

        Permission first =
                permission(
                        UUID.randomUUID(),
                        "case.review"
                );

        Permission second =
                permission(
                        UUID.randomUUID(),
                        "permission.view"
                );

        when(
                fixture.repository.findAll()
        ).thenReturn(
                List.of(
                        first,
                        second
                )
        );

        List<Permission> result =
                fixture.service.getPermissions(
                        fixture.securityContext
                );

        assertEquals(
                2,
                result.size()
        );

        assertSame(
                first,
                result.get(0)
        );

        assertSame(
                second,
                result.get(1)
        );

        verify(
                fixture.repository,
                never()
        ).save(
                any(Permission.class)
        );
    }

    @Test
    void viewByIdShouldRejectMissingViewPermission() {

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

        verify(
                fixture.repository,
                never()
        ).findById(
                any(UUID.class)
        );
    }

    @Test
    void viewCatalogShouldRejectMissingViewPermission() {

        Fixture fixture =
                fixture("permission.manage");

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.getPermissions(
                                fixture.securityContext
                        )
        );

        verify(
                fixture.repository,
                never()
        ).findAll();
    }

    @Test
    void viewByIdShouldRejectUnknownPermission() {

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
    }

    private static Permission permission(
            UUID permissionId,
            String permissionCode) {

        Permission permission =
                new Permission();

        permission.setPermissionId(
                permissionId
        );

        permission.setPermissionCode(
                permissionCode
        );

        permission.setPermissionName(
                permissionCode
        );

        permission.setResource(
                "case"
        );

        permission.setAction(
                "review"
        );

        return permission;
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