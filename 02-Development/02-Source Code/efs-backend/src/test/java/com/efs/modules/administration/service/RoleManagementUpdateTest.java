package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Role;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleManagementUpdateTest {

    @Test
    void updateShouldModifyAllowedFieldsAndPreserveControlledFields() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        LocalDateTime createdAt =
                LocalDateTime.of(
                        2026,
                        1,
                        15,
                        10,
                        30
                );

        Role existing =
                role(
                        roleId,
                        fixture.organizationId,
                        "FRAUD_ANALYST",
                        "Fraud Analyst",
                        "Original description",
                        false,
                        "ACTIVE",
                        createdAt
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(existing)
        );

        when(
                fixture.repository
                        .existsByRoleCodeAndRoleIdNot(
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
                        existing
                )
        ).thenReturn(
                existing
        );

        Role updated =
                fixture.service.updateRole(
                        roleId,
                        "FRAUD_SUPERVISOR",
                        "Fraud Supervisor",
                        "Updated description",
                        fixture.securityContext
                );

        assertSame(
                existing,
                updated
        );

        assertEquals(
                roleId,
                updated.getRoleId()
        );

        assertEquals(
                fixture.organizationId,
                updated.getOrganizationId()
        );

        assertFalse(
                updated.getSystem()
        );

        assertEquals(
                "ACTIVE",
                updated.getStatus()
        );

        assertEquals(
                createdAt,
                updated.getCreatedAt()
        );

        assertEquals(
                "FRAUD_SUPERVISOR",
                updated.getRoleCode()
        );

        assertEquals(
                "Fraud Supervisor",
                updated.getRoleName()
        );

        assertEquals(
                "Updated description",
                updated.getDescription()
        );

        verify(
                fixture.repository
        ).save(
                existing
        );
    }

    @Test
    void updateShouldRejectMissingManagePermission() {

        Fixture fixture =
                fixture();

        UUID roleId =
                UUID.randomUUID();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.updateRole(
                                roleId,
                                "FRAUD_ANALYST",
                                "Fraud Analyst",
                                null,
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
    void updateShouldRejectMissingRole() {

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
                                "FRAUD_ANALYST",
                                "Fraud Analyst",
                                null,
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
    void updateShouldRejectRoleFromAnotherOrganization() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role existing =
                role(
                        roleId,
                        UUID.randomUUID(),
                        "FRAUD_ANALYST",
                        "Fraud Analyst",
                        null,
                        false,
                        "ACTIVE",
                        LocalDateTime.now()
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(existing)
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

        verify(
                fixture.repository,
                never()
        ).existsByRoleCodeAndRoleIdNot(
                any(String.class),
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
    void updateShouldRejectDuplicateRoleCode() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role existing =
                role(
                        roleId,
                        fixture.organizationId,
                        "FRAUD_ANALYST",
                        "Fraud Analyst",
                        null,
                        false,
                        "ACTIVE",
                        LocalDateTime.now()
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(existing)
        );

        when(
                fixture.repository
                        .existsByRoleCodeAndRoleIdNot(
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

        verify(
                fixture.repository,
                never()
        ).save(
                any(Role.class)
        );
    }

    @Test
    void updateShouldRejectDuplicateRoleNameWithinOrganization() {

        Fixture fixture =
                fixture("role.manage");

        UUID roleId =
                UUID.randomUUID();

        Role existing =
                role(
                        roleId,
                        fixture.organizationId,
                        "FRAUD_ANALYST",
                        "Fraud Analyst",
                        null,
                        false,
                        "ACTIVE",
                        LocalDateTime.now()
                );

        when(
                fixture.repository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(existing)
        );

        when(
                fixture.repository
                        .existsByRoleCodeAndRoleIdNot(
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
            String roleCode,
            String roleName,
            String description,
            boolean system,
            String status,
            LocalDateTime createdAt) {

        Role role =
                new Role();

        role.setRoleId(
                roleId
        );

        role.setOrganizationId(
                organizationId
        );

        role.setRoleCode(
                roleCode
        );

        role.setRoleName(
                roleName
        );

        role.setDescription(
                description
        );

        role.setSystem(
                system
        );

        role.setStatus(
                status
        );

        role.setCreatedAt(
                createdAt
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