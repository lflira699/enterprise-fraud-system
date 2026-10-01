package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Permission;
import com.efs.modules.administration.repository.PermissionRepository;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
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

class PermissionManagementUpdateTest {

    @Test
    void updateShouldModifyOnlyMutableFields() {

        Fixture fixture =
                fixture("permission.manage");

        UUID permissionId =
                UUID.randomUUID();

        LocalDateTime createdAt =
                LocalDateTime.of(
                        2026,
                        10,
                        1,
                        12,
                        0
                );

        Permission permission =
                permission(
                        permissionId,
                        "case.review",
                        "Review Case",
                        "case",
                        "review",
                        "Original description",
                        createdAt
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

        Permission result =
                fixture.service.updatePermission(
                        permissionId,
                        "Review Investigation Case",
                        "Updated description",
                        fixture.securityContext
                );

        assertSame(
                permission,
                result
        );

        assertEquals(
                "Review Investigation Case",
                result.getPermissionName()
        );

        assertEquals(
                "Updated description",
                result.getDescription()
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
                "case",
                result.getResource()
        );

        assertEquals(
                "review",
                result.getAction()
        );

        assertEquals(
                createdAt,
                result.getCreatedAt()
        );
    }

    @Test
    void updateShouldRejectUnknownPermission() {

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

        verify(
                fixture.repository,
                never()
        ).save(
                any(Permission.class)
        );
    }

    @Test
    void updateShouldRejectMissingManagePermission() {

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

        verify(
                fixture.repository,
                never()
        ).findById(
                any(UUID.class)
        );

        verify(
                fixture.repository,
                never()
        ).save(
                any(Permission.class)
        );
    }

    @Test
    void updateShouldRejectBlankPermissionName() {

        Fixture fixture =
                fixture("permission.manage");

        UUID permissionId =
                UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        fixture.service.updatePermission(
                                permissionId,
                                " ",
                                null,
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
    void updateShouldRejectNullPermissionId() {

        Fixture fixture =
                fixture("permission.manage");

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        fixture.service.updatePermission(
                                null,
                                "Updated Permission",
                                null,
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

    private static Permission permission(
            UUID permissionId,
            String permissionCode,
            String permissionName,
            String resource,
            String action,
            String description,
            LocalDateTime createdAt) {

        Permission permission =
                new Permission();

        permission.setPermissionId(
                permissionId
        );

        permission.setPermissionCode(
                permissionCode
        );

        permission.setPermissionName(
                permissionName
        );

        permission.setResource(
                resource
        );

        permission.setAction(
                action
        );

        permission.setDescription(
                description
        );

        permission.setCreatedAt(
                createdAt
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