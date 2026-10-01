package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Role;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

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

class RoleManagementLifecycleTest {

    @Test
    void enableShouldActivateRoleWithinAuthorizedOrganization() {

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

        Role enabled =
                fixture.service.enableRole(
                        roleId,
                        fixture.securityContext
                );

        assertSame(
                role,
                enabled
        );

        assertEquals(
                "ACTIVE",
                enabled.getStatus()
        );

        verify(
                fixture.repository
        ).save(
                role
        );
    }

    @Test
    void disableShouldDeactivateRoleWithinAuthorizedOrganization() {

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

        Role disabled =
                fixture.service.disableRole(
                        roleId,
                        fixture.securityContext
                );

        assertSame(
                role,
                disabled
        );

        assertEquals(
                "INACTIVE",
                disabled.getStatus()
        );

        verify(
                fixture.repository
        ).save(
                role
        );
    }

    @Test
    void enableShouldRejectMissingManagePermission() {

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

        verify(
                fixture.lookupService,
                never()
        ).getAuthorizedUser(
                any(UUID.class)
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
                any(Role.class)
        );
    }

    @Test
    void disableShouldRejectMissingManagePermission() {

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

        verify(
                fixture.lookupService,
                never()
        ).getAuthorizedUser(
                any(UUID.class)
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
                any(Role.class)
        );
    }

    @Test
    void enableShouldRejectMissingRole() {

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

        verify(
                fixture.repository,
                never()
        ).save(
                any(Role.class)
        );
    }

    @Test
    void disableShouldRejectMissingRole() {

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

        verify(
                fixture.repository,
                never()
        ).save(
                any(Role.class)
        );
    }

    @Test
    void enableShouldRejectRoleFromAnotherOrganization() {

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

        assertEquals(
                "INACTIVE",
                role.getStatus()
        );

        verify(
                fixture.repository,
                never()
        ).save(
                any(Role.class)
        );
    }

    @Test
    void disableShouldRejectRoleFromAnotherOrganization() {

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

        assertEquals(
                "ACTIVE",
                role.getStatus()
        );

        verify(
                fixture.repository,
                never()
        ).save(
                any(Role.class)
        );
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

        AuditEventServiceInterface auditEventService =
                mock(
                        AuditEventServiceInterface.class
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
            SecurityContext securityContext,
            UUID organizationId) {
    }
}