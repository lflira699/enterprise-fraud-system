package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Role;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleManagementCreateTest {

    @Test
    void createShouldPersistActiveOrganizationRole() {

        Fixture fixture =
                fixture("role.manage");

        when(
                fixture.repository.existsByRoleCode(
                        "FRAUD_ANALYST"
                )
        ).thenReturn(false);

        when(
                fixture.repository
                        .existsByRoleNameAndOrganizationId(
                                "Fraud Analyst",
                                fixture.organizationId
                        )
        ).thenReturn(false);

        when(
                fixture.repository.save(
                        any(Role.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        Role created =
                fixture.service.createRole(
                        "FRAUD_ANALYST",
                        "Fraud Analyst",
                        "Fraud investigation role",
                        fixture.securityContext
                );

        assertNotNull(
                created
        );

        assertEquals(
                fixture.organizationId,
                created.getOrganizationId()
        );

        assertEquals(
                "FRAUD_ANALYST",
                created.getRoleCode()
        );

        assertEquals(
                "Fraud Analyst",
                created.getRoleName()
        );

        assertEquals(
                "Fraud investigation role",
                created.getDescription()
        );

        assertFalse(
                created.getSystem()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        verify(
                fixture.repository
        ).save(
                created
        );
    }

    @Test
    void createShouldRejectMissingManagePermission() {

        Fixture fixture =
                fixture();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.createRole(
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
        ).save(
                any(Role.class)
        );
    }

    @Test
    void createShouldRejectDuplicateRoleCode() {

        Fixture fixture =
                fixture("role.manage");

        when(
                fixture.repository.existsByRoleCode(
                        "FRAUD_ANALYST"
                )
        ).thenReturn(true);

        assertThrows(
                DuplicateRecordException.class,
                () ->
                        fixture.service.createRole(
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
    void createShouldRejectDuplicateRoleNameWithinOrganization() {

        Fixture fixture =
                fixture("role.manage");

        when(
                fixture.repository.existsByRoleCode(
                        "FRAUD_ANALYST"
                )
        ).thenReturn(false);

        when(
                fixture.repository
                        .existsByRoleNameAndOrganizationId(
                                "Fraud Analyst",
                                fixture.organizationId
                        )
        ).thenReturn(true);

        assertThrows(
                DuplicateRecordException.class,
                () ->
                        fixture.service.createRole(
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
    void createShouldRejectBlankRoleCode() {

        Fixture fixture =
                fixture("role.manage");

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        fixture.service.createRole(
                                " ",
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
    void createShouldRejectBlankRoleName() {

        Fixture fixture =
                fixture("role.manage");

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        fixture.service.createRole(
                                "FRAUD_ANALYST",
                                " ",
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

    private record Fixture(
            RoleManagementService service,
            RoleRepository repository,
            UserAccountLookupServiceInterface lookupService,
            SecurityContext securityContext,
            UUID organizationId) {
    }
}