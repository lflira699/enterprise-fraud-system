package com.efs.modules.administration.service;

import com.efs.shared.security.SecurityContext;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserAccount;
import com.efs.modules.administration.repository.UserAccountRepository;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAdministrationUpdateFailurePathTest {

    @Test
    void updateShouldRejectMissingTargetUser() {

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
        UUID targetUserId = UUID.randomUUID();
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
                repository.findById(targetUserId)
        ).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateUser(
                        targetUserId,
                        tenantId,
                        null,
                        "username",
                        "Full Name",
                        "user@example.com",
                        "LOCAL",
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
    void updateShouldRejectCurrentCrossOrganizationTarget() {

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
        UUID targetUserId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        UUID.randomUUID(),
                        null,
                        "actor@example.com"
                );

        UserAccount target =
                new UserAccount();

        target.setUserId(targetUserId);
        target.setOrganizationId(
                UUID.randomUUID()
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        assertThrows(
                AccessDeniedException.class,
                () -> service.updateUser(
                        targetUserId,
                        UUID.randomUUID(),
                        null,
                        "username",
                        "Full Name",
                        "user@example.com",
                        "LOCAL",
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
    void updateShouldRejectCurrentCrossTenantTargetForTenantScopedActor() {

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
        UUID targetUserId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        actorTenantId,
                        "actor@example.com"
                );

        UserAccount target =
                new UserAccount();

        target.setUserId(targetUserId);
        target.setOrganizationId(
                organizationId
        );
        target.setTenantId(
                UUID.randomUUID()
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        assertThrows(
                AccessDeniedException.class,
                () -> service.updateUser(
                        targetUserId,
                        actorTenantId,
                        null,
                        "username",
                        "Full Name",
                        "user@example.com",
                        "LOCAL",
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
    void updateShouldRejectDestinationTenantOutsideAuthorizedOrganization() {

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
        UUID targetUserId = UUID.randomUUID();
        UUID destinationTenantId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        null,
                        "actor@example.com"
                );

        UserAccount target =
                new UserAccount();

        target.setUserId(targetUserId);
        target.setOrganizationId(
                organizationId
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        when(
                tenantLookup.getOrganizationIdByTenantId(
                        destinationTenantId
                )
        ).thenReturn(
                UUID.randomUUID()
        );

        assertThrows(
                AccessDeniedException.class,
                () -> service.updateUser(
                        targetUserId,
                        destinationTenantId,
                        null,
                        "username",
                        "Full Name",
                        "user@example.com",
                        "LOCAL",
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
    void updateShouldRejectDestinationTenantChangeForTenantScopedActor() {

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
        UUID targetUserId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        actorTenantId,
                        "actor@example.com"
                );

        UserAccount target =
                new UserAccount();

        target.setUserId(targetUserId);
        target.setOrganizationId(
                organizationId
        );
        target.setTenantId(
                actorTenantId
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        assertThrows(
                AccessDeniedException.class,
                () -> service.updateUser(
                        targetUserId,
                        UUID.randomUUID(),
                        null,
                        "username",
                        "Full Name",
                        "user@example.com",
                        "LOCAL",
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
    void updateShouldAllowAuthorizedDestinationTenant() {

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
        UUID currentTenantId = UUID.randomUUID();
        UUID destinationTenantId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        null,
                        "actor@example.com"
                );

        UserAccount target =
                new UserAccount();

        target.setUserId(targetUserId);
        target.setOrganizationId(
                organizationId
        );
        target.setTenantId(
                currentTenantId
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        when(
                tenantLookup.getOrganizationIdByTenantId(
                        destinationTenantId
                )
        ).thenReturn(
                organizationId
        );

        when(
                repository.save(target)
        ).thenReturn(target);

        UserAccount result =
                service.updateUser(
                        targetUserId,
                        destinationTenantId,
                        null,
                        "updated-user",
                        "Updated User",
                        "updated@example.com",
                        "LOCAL",
                        true,
                        manageContext(actorUserId)
                );

        assertEquals(
                destinationTenantId,
                result.getTenantId()
        );

        assertEquals(
                "updated-user",
                result.getUsername()
        );

        verify(
                tenantLookup
        ).getOrganizationIdByTenantId(
                destinationTenantId
        );

        verify(
                repository
        ).save(target);
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

    @Test
    void updateShouldRejectMissingManagePermission() {

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
                () -> service.updateUser(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        "username",
                        "Full Name",
                        "user@example.com",
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
        ).findById(
                any(UUID.class)
        );

        verify(
                repository,
                never()
        ).save(
                any(UserAccount.class)
        );
    }

    @Test
    void updateShouldAllowOrganizationScopedDestinationWithoutTenant() {

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
        UUID targetUserId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        null,
                        "actor@example.com"
                );

        UserAccount target =
                new UserAccount();

        target.setUserId(targetUserId);
        target.setOrganizationId(organizationId);
        target.setTenantId(UUID.randomUUID());

        when(
                lookupService.getAuthorizedUser(
                        actorUserId
                )
        ).thenReturn(actor);

        when(
                repository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.of(target)
        );

        when(
                repository.save(target)
        ).thenReturn(target);

        UserAccount result =
                service.updateUser(
                        targetUserId,
                        null,
                        null,
                        "updated-user",
                        "Updated User",
                        "updated@example.com",
                        "LOCAL",
                        true,
                        manageContext(actorUserId)
                );

        assertEquals(
                null,
                result.getTenantId()
        );

        verify(
                tenantLookup,
                never()
        ).getOrganizationIdByTenantId(
                any(UUID.class)
        );

        verify(
                repository
        ).save(target);
    }

    @Test
    void updateShouldRejectOrganizationScopeDestinationForTenantScopedActor() {

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
        UUID targetUserId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        actorTenantId,
                        "actor@example.com"
                );

        UserAccount target =
                new UserAccount();

        target.setUserId(targetUserId);
        target.setOrganizationId(organizationId);
        target.setTenantId(actorTenantId);

        when(
                lookupService.getAuthorizedUser(
                        actorUserId
                )
        ).thenReturn(actor);

        when(
                repository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.of(target)
        );

        assertThrows(
                AccessDeniedException.class,
                () -> service.updateUser(
                        targetUserId,
                        null,
                        null,
                        "updated-user",
                        "Updated User",
                        "updated@example.com",
                        "LOCAL",
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
        ).save(
                any(UserAccount.class)
        );
    }
}