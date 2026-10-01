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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleManagementViewTest {

    @Test
    void viewShouldReturnRoleWithinAuthorizedOrganization() {

        Fixture fixture =
                fixture("role.view");

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

        Role result =
                fixture.service.getRole(
                        roleId,
                        fixture.securityContext
                );

        assertSame(
                role,
                result
        );

        verify(
                fixture.repository
        ).findById(
                roleId
        );

        verify(
                fixture.repository,
                never()
        ).save(
                any(Role.class)
        );
    }

    @Test
    void viewShouldRejectMissingViewPermission() {

        Fixture fixture =
                fixture();

        UUID roleId =
                UUID.randomUUID();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.getRole(
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
    void viewShouldRejectMissingRole() {

        Fixture fixture =
                fixture("role.view");

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
                        fixture.service.getRole(
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
    void viewShouldRejectRoleFromAnotherOrganization() {

        Fixture fixture =
                fixture("role.view");

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
                        fixture.service.getRole(
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
                "ROLE_CODE"
        );

        role.setRoleName(
                "Role Name"
        );

        role.setSystem(
                false
        );

        role.setStatus(
                "ACTIVE"
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