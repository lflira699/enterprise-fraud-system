package com.efs.modules.administration.service;

import com.efs.shared.security.SecurityContext;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserAccount;
import com.efs.modules.administration.repository.UserAccountRepository;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAdministrationCreateFailurePathTest {

    @Test
    void createShouldRejectTenantOutsideAuthorizedOrganization() {

        UserAccountRepository repository =
                mock(UserAccountRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        TenantOrganizationLookupServiceInterface tenantLookup =
                mock(TenantOrganizationLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

        UserAdministrationService service =
                new UserAdministrationService(
                        repository,
                        lookupService,
                        tenantLookup,
                        auditService
                );

        UUID actorUserId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        null,
                        "actor@example.com"
                )
        );

        when(
                tenantLookup.getOrganizationIdByTenantId(
                        tenantId
                )
        ).thenReturn(
                UUID.randomUUID()
        );

        assertThrows(
                AccessDeniedException.class,
                () -> service.createUser(
                        UUID.randomUUID(),
                        tenantId,
                        null,
                        "analyst01",
                        "Analyst One",
                        "analyst01@example.com",
                        "FEDERATED",
                        true,
                        manageContext(actorUserId)
                )
        );

        verify(
                repository,
                never()
        ).save(any(UserAccount.class));
    }

    @Test
    void createShouldRejectAnotherTenantForTenantScopedActor() {

        UserAccountRepository repository =
                mock(UserAccountRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        TenantOrganizationLookupServiceInterface tenantLookup =
                mock(TenantOrganizationLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

        UserAdministrationService service =
                new UserAdministrationService(
                        repository,
                        lookupService,
                        tenantLookup,
                        auditService
                );

        UUID actorUserId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID actorTenantId = UUID.randomUUID();
        UUID requestedTenantId = UUID.randomUUID();

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        actorTenantId,
                        "actor@example.com"
                )
        );

        assertThrows(
                AccessDeniedException.class,
                () -> service.createUser(
                        UUID.randomUUID(),
                        requestedTenantId,
                        null,
                        "analyst01",
                        "Analyst One",
                        "analyst01@example.com",
                        "FEDERATED",
                        true,
                        manageContext(actorUserId)
                )
        );

        verify(
                tenantLookup,
                never()
        ).getOrganizationIdByTenantId(
                any(UUID.class)
        );

        verify(
                repository,
                never()
        ).save(any(UserAccount.class));
    }

    @Test
    void createShouldAllowOwnTenantForTenantScopedActor() {

        UserAccountRepository repository =
                mock(UserAccountRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        TenantOrganizationLookupServiceInterface tenantLookup =
                mock(TenantOrganizationLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

        UserAdministrationService service =
                new UserAdministrationService(
                        repository,
                        lookupService,
                        tenantLookup,
                        auditService
                );

        UUID actorUserId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        tenantId,
                        "actor@example.com"
                )
        );

        when(
                tenantLookup.getOrganizationIdByTenantId(
                        tenantId
                )
        ).thenReturn(
                organizationId
        );

        when(
                repository.save(
                        any(UserAccount.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        UserAccount created =
                service.createUser(
                        userId,
                        tenantId,
                        null,
                        "analyst01",
                        "Analyst One",
                        "analyst01@example.com",
                        "FEDERATED",
                        true,
                        manageContext(actorUserId)
                );

        assertEquals(
                userId,
                created.getUserId()
        );

        assertEquals(
                organizationId,
                created.getOrganizationId()
        );

        assertEquals(
                tenantId,
                created.getTenantId()
        );

        verify(
                repository
        ).save(any(UserAccount.class));
    }

    @Test
    void createShouldAllowOrganizationScopedActorWithinOrganization() {

        UserAccountRepository repository =
                mock(UserAccountRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        TenantOrganizationLookupServiceInterface tenantLookup =
                mock(TenantOrganizationLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

        UserAdministrationService service =
                new UserAdministrationService(
                        repository,
                        lookupService,
                        tenantLookup,
                        auditService
                );

        UUID actorUserId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        null,
                        "actor@example.com"
                )
        );

        when(
                tenantLookup.getOrganizationIdByTenantId(
                        tenantId
                )
        ).thenReturn(
                organizationId
        );

        when(
                repository.save(
                        any(UserAccount.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        UserAccount created =
                service.createUser(
                        userId,
                        tenantId,
                        null,
                        "analyst01",
                        "Analyst One",
                        "analyst01@example.com",
                        "FEDERATED",
                        true,
                        manageContext(actorUserId)
                );

        assertEquals(
                organizationId,
                created.getOrganizationId()
        );

        assertEquals(
                tenantId,
                created.getTenantId()
        );

        verify(
                tenantLookup
        ).getOrganizationIdByTenantId(
                tenantId
        );

        verify(
                repository
        ).save(any(UserAccount.class));
    }

    @Test
    void createShouldRejectMissingManagePermission() {

        UserAccountRepository repository =
                mock(UserAccountRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        TenantOrganizationLookupServiceInterface tenantLookup =
                mock(TenantOrganizationLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

        UserAdministrationService service =
                new UserAdministrationService(
                        repository,
                        lookupService,
                        tenantLookup,
                        auditService
                );

        UUID actorUserId = UUID.randomUUID();

        SecurityContext securityContext =
                new SecurityContext(
                        actorUserId,
                        null,
                        null,
                        Set.of(),
                        Set.of(),
                        Set.of()
                );

        assertThrows(
                AccessDeniedException.class,
                () -> service.createUser(
                        UUID.randomUUID(),
                        null,
                        null,
                        "analyst01",
                        "Analyst One",
                        "analyst01@example.com",
                        "FEDERATED",
                        true,
                        securityContext
                )
        );

        verify(
                lookupService,
                never()
        ).getAuthorizedUser(
                any(UUID.class)
        );

        verify(
                repository,
                never()
        ).save(
                any(UserAccount.class)
        );
    }

    private SecurityContext manageContext(
            UUID actorUserId) {

        return new SecurityContext(
                actorUserId,
                null,
                null,
                Set.of(),
                Set.of("user.manage"),
                Set.of()
        );
    }
}