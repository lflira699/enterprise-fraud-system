package com.efs.modules.administration.service;

import com.efs.shared.security.SecurityContext;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserAccount;
import com.efs.modules.administration.repository.UserAccountRepository;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAdministrationServiceTest {

    @Test
    void createUserPersistsActiveInstitutionalUserAccount()
            throws Exception {

        UserAccountRepository repository =
                mock(UserAccountRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        TenantOrganizationLookupServiceInterface tenantOrganizationLookupService =
                mock(TenantOrganizationLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

        UserAdministrationService service =
                new UserAdministrationService(
                        repository,
                        lookupService,
                        tenantOrganizationLookupService,
                        auditService
                );

        UUID actorUserId = UUID.randomUUID();

        SecurityContext securityContext =
                new SecurityContext(
                        actorUserId,
                        null,
                        null,
                        Set.of(),
                        Set.of("user.manage"),
                        Set.of()
                );
        UUID organizationId = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        null,
                        "actor@example.com"
                );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.save(org.mockito.ArgumentMatchers.any(UserAccount.class))
        ).thenAnswer(invocation -> invocation.getArgument(0));

        Method createUser =
                UserAdministrationService.class.getDeclaredMethod(
                        "createUser",
                        UUID.class,
                        UUID.class,
                        UUID.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        boolean.class,
                        SecurityContext.class
                );

        assertNotNull(createUser);

        UserAccount created =
                (UserAccount) createUser.invoke(
                        service,
                        newUserId,
                        null,
                        null,
                        "analyst01",
                        "Fraud Analyst",
                        "analyst@example.com",
                        "FEDERATED",
                        true,
                        securityContext
                );

        assertNotNull(created);

        assertEquals(
                newUserId,
                created.getUserId()
        );

        assertEquals(
                organizationId,
                created.getOrganizationId()
        );

        assertEquals(
                "analyst01",
                created.getUsername()
        );

        assertEquals(
                "Fraud Analyst",
                created.getFullName()
        );

        assertEquals(
                "analyst@example.com",
                created.getEmail()
        );

        assertEquals(
                "FEDERATED",
                created.getAuthenticationProvider()
        );

        assertEquals(
                true,
                created.getMfaEnabled()
        );

        assertEquals(
                "ACTIVE",
                created.getAccountStatus()
        );

        assertEquals(
                0,
                created.getFailedLoginAttempts()
        );

        verify(repository).save(created);
    }

    @Test
    void shouldUpdateUserWithinAuthorizedOrganization()
            throws Exception {

                UserAccountRepository repository =
                mock(UserAccountRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        TenantOrganizationLookupServiceInterface tenantOrganizationLookupService =
                mock(TenantOrganizationLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

        UserAdministrationService service =
                new UserAdministrationService(
                        repository,
                        lookupService,
                        tenantOrganizationLookupService,
                        auditService
                );
UUID actorUserId =
                UUID.randomUUID();

        SecurityContext securityContext =
                new SecurityContext(
                        actorUserId,
                        null,
                        null,
                        Set.of(),
                        Set.of("user.manage"),
                        Set.of()
                );

        UUID organizationId =
                UUID.randomUUID();

        UUID targetUserId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        UUID businessUnitId =
                UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        null,
                        "actor@example.com"
                );

        UserAccount existing =
                new UserAccount();

        existing.setUserId(
                targetUserId
        );

        existing.setOrganizationId(
                organizationId
        );

        existing.setUsername(
                "old-user"
        );

        existing.setFullName(
                "Old Name"
        );

        existing.setEmail(
                "old@example.com"
        );

        existing.setAuthenticationProvider(
                "FEDERATED"
        );

        existing.setMfaEnabled(
                false
        );

        existing.setAccountStatus(
                "ACTIVE"
        );

        when(
                lookupService.getAuthorizedUser(
                        actorUserId
                )
        ).thenReturn(
                actor
        );

        when(
                repository.findById(
                        targetUserId
                )
        ).thenReturn(
                java.util.Optional.of(
                        existing
                )
        );

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                tenantId
                        )
        ).thenReturn(
                organizationId
        );

        when(
                repository.save(
                        org.mockito.ArgumentMatchers.any(
                                UserAccount.class
                        )
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        java.lang.reflect.Method method =
                UserAdministrationService.class
                        .getMethod(
                                "updateUser",
                                UUID.class,
                                UUID.class,
                                UUID.class,
                                String.class,
                                String.class,
                                String.class,
                                String.class,
                                Boolean.class,
                                SecurityContext.class
                        );

        UserAccount updated =
                (UserAccount) method.invoke(
                        service,
                        targetUserId,
                        tenantId,
                        businessUnitId,
                        "analyst02",
                        "Updated Analyst",
                        "updated@example.com",
                        "FEDERATED",
                        Boolean.TRUE,
                        securityContext
                );

        assertEquals(
                targetUserId,
                updated.getUserId()
        );

        assertEquals(
                organizationId,
                updated.getOrganizationId()
        );

        assertEquals(
                tenantId,
                updated.getTenantId()
        );

        assertEquals(
                businessUnitId,
                updated.getBusinessUnitId()
        );

        assertEquals(
                "analyst02",
                updated.getUsername()
        );

        assertEquals(
                "Updated Analyst",
                updated.getFullName()
        );

        assertEquals(
                "updated@example.com",
                updated.getEmail()
        );

        assertEquals(
                "FEDERATED",
                updated.getAuthenticationProvider()
        );

        assertEquals(
                Boolean.TRUE,
                updated.getMfaEnabled()
        );

        assertEquals(
                "ACTIVE",
                updated.getAccountStatus()
        );

        verify(
                lookupService
        ).getAuthorizedUser(
                actorUserId
        );

        verify(
                repository
        ).findById(
                targetUserId
        );

        verify(
                repository
        ).save(
                existing
        );
    }

    @Test
    void shouldEnableInactiveUserWithinAuthorizedOrganization()
            throws Exception {

        UUID actorUserId =
                UUID.randomUUID();

        SecurityContext securityContext =
                new SecurityContext(
                        actorUserId,
                        null,
                        null,
                        Set.of(),
                        Set.of("user.manage"),
                        Set.of()
                );
        UUID organizationId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        null,
                        "actor@example.com"
                );

        UserAccount targetUser =
                new UserAccount();

        targetUser.setUserId(targetUserId);
        targetUser.setOrganizationId(organizationId);
        targetUser.setAccountStatus("INACTIVE");

        UserAccountRepository repository =
                mock(UserAccountRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        TenantOrganizationLookupServiceInterface tenantOrganizationLookupService =
                mock(TenantOrganizationLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(
                java.util.Optional.of(targetUser)
        );

        when(
                repository.save(
                        org.mockito.ArgumentMatchers.any(
                                UserAccount.class
                        )
                )
        ).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        UserAdministrationService service =
                new UserAdministrationService(
                        repository,
                        lookupService,
                        tenantOrganizationLookupService,
                        auditService
                );

        java.lang.reflect.Method method =
                UserAdministrationService.class
                        .getDeclaredMethod(
                                "enableUser",
                                UUID.class,
                                SecurityContext.class
                        );

        Object result =
                method.invoke(
                        service,
                        targetUserId,
                        securityContext
                );

        assertNotNull(result);

        UserAccount enabledUser =
                (UserAccount) result;

        assertEquals(
                "ACTIVE",
                enabledUser.getAccountStatus()
        );

        assertEquals(
                targetUserId,
                enabledUser.getUserId()
        );

        assertEquals(
                organizationId,
                enabledUser.getOrganizationId()
        );

        verify(
                lookupService
        ).getAuthorizedUser(actorUserId);

        verify(
                repository
        ).findById(targetUserId);

        verify(
                repository
        ).save(targetUser);
    }

    @Test
    void shouldDisableActiveUserWithinAuthorizedOrganization()
            throws Exception {

        UUID actorUserId =
                UUID.randomUUID();

        SecurityContext securityContext =
                new SecurityContext(
                        actorUserId,
                        null,
                        null,
                        Set.of(),
                        Set.of("user.manage"),
                        Set.of()
                );
        UUID organizationId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        null,
                        "actor@example.com"
                );

        UserAccount targetUser =
                new UserAccount();

        targetUser.setUserId(targetUserId);
        targetUser.setOrganizationId(organizationId);
        targetUser.setAccountStatus("ACTIVE");

        UserAccountRepository repository =
                mock(UserAccountRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        TenantOrganizationLookupServiceInterface tenantOrganizationLookupService =
                mock(TenantOrganizationLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(
                java.util.Optional.of(targetUser)
        );

        when(
                repository.save(
                        org.mockito.ArgumentMatchers.any(
                                UserAccount.class
                        )
                )
        ).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        UserAdministrationService service =
                new UserAdministrationService(
                        repository,
                        lookupService,
                        tenantOrganizationLookupService,
                        auditService
                );

        java.lang.reflect.Method method =
                UserAdministrationService.class
                        .getDeclaredMethod(
                                "disableUser",
                                UUID.class,
                                SecurityContext.class
                        );

        Object result =
                method.invoke(
                        service,
                        targetUserId,
                        securityContext
                );

        assertNotNull(result);

        UserAccount disabledUser =
                (UserAccount) result;

        assertEquals(
                "INACTIVE",
                disabledUser.getAccountStatus()
        );

        assertEquals(
                targetUserId,
                disabledUser.getUserId()
        );

        assertEquals(
                organizationId,
                disabledUser.getOrganizationId()
        );

        verify(
                lookupService
        ).getAuthorizedUser(actorUserId);

        verify(
                repository
        ).findById(targetUserId);

        verify(
                repository
        ).save(targetUser);
    }

    @Test
    void shouldConsultUserWithinAuthorizedOrganization() throws Exception {
        UserAccountRepository repository =
                mock(UserAccountRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        TenantOrganizationLookupServiceInterface tenantOrganizationLookupService =
                mock(TenantOrganizationLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

        UserAdministrationService service =
                new UserAdministrationService(
                        repository,
                        lookupService,
                        tenantOrganizationLookupService,
                        auditService
                );

        UUID organizationId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        UUID actorUserId = UUID.randomUUID();

        SecurityContext securityContext =
                new SecurityContext(
                        actorUserId,
                        null,
                        null,
                        Set.of(),
                        Set.of("user.view"),
                        Set.of()
                );

        UserAccountReference actor =
                mock(UserAccountReference.class);

        UserAccount targetUser =
                new UserAccount();

        targetUser.setUserId(targetUserId);
        targetUser.setOrganizationId(organizationId);

        when(actor.organizationId())
                .thenReturn(organizationId);

        when(lookupService.getAuthorizedUser(actorUserId))
                .thenReturn(actor);

        when(repository.findById(targetUserId))
                .thenReturn(java.util.Optional.of(targetUser));

        Method method =
                UserAdministrationService.class.getMethod(
                        "getUser",
                        UUID.class,
                        SecurityContext.class
                );

        Object result =
                method.invoke(
                        service,
                        targetUserId,
                        securityContext
                );

        org.junit.jupiter.api.Assertions.assertSame(targetUser, result);

        verify(lookupService)
                .getAuthorizedUser(actorUserId);

        verify(repository)
                .findById(targetUserId);

        verify(repository, org.mockito.Mockito.never())
                .save(org.mockito.ArgumentMatchers.any(UserAccount.class));
    }

}