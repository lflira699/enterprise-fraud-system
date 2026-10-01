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

class UserAdministrationDisableFailurePathTest {

    @Test
    void disableShouldRejectMissingTargetUser() {

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
        UUID organizationId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

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
                () -> service.disableUser(
                        targetUserId,
                        manageContext(actorUserId)
                )
        );

        verify(
                repository,
                never()
        ).save(any(UserAccount.class));
    }

    @Test
    void disableShouldRejectCrossOrganizationTarget() {

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
        target.setAccountStatus(
                "ACTIVE"
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        assertThrows(
                AccessDeniedException.class,
                () -> service.disableUser(
                        targetUserId,
                        manageContext(actorUserId)
                )
        );

        assertEquals(
                "ACTIVE",
                target.getAccountStatus()
        );

        verify(
                repository,
                never()
        ).save(any(UserAccount.class));
    }

    @Test
    void disableShouldRejectCrossTenantTargetForTenantScopedActor() {

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
        target.setAccountStatus(
                "ACTIVE"
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        assertThrows(
                AccessDeniedException.class,
                () -> service.disableUser(
                        targetUserId,
                        manageContext(actorUserId)
                )
        );

        assertEquals(
                "ACTIVE",
                target.getAccountStatus()
        );

        verify(
                repository,
                never()
        ).save(any(UserAccount.class));
    }

    @Test
    void disableShouldAllowOwnTenantTarget() {

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
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        tenantId,
                        "actor@example.com"
                );

        UserAccount target =
                new UserAccount();

        target.setUserId(targetUserId);
        target.setOrganizationId(
                organizationId
        );
        target.setTenantId(
                tenantId
        );
        target.setAccountStatus(
                "ACTIVE"
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        when(
                repository.save(target)
        ).thenReturn(target);

        UserAccount result =
                service.disableUser(
                        targetUserId,
                        manageContext(actorUserId)
                );

        assertEquals(
                "INACTIVE",
                result.getAccountStatus()
        );

        verify(
                repository
        ).save(target);
    }

    @Test
    void disableShouldAllowOrganizationScopedActorWithinOrganization() {

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
        target.setOrganizationId(
                organizationId
        );
        target.setTenantId(
                UUID.randomUUID()
        );
        target.setAccountStatus(
                "ACTIVE"
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        when(
                repository.save(target)
        ).thenReturn(target);

        UserAccount result =
                service.disableUser(
                        targetUserId,
                        manageContext(actorUserId)
                );

        assertEquals(
                "INACTIVE",
                result.getAccountStatus()
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
    void disableShouldRejectMissingManagePermission() {

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
                () -> service.disableUser(
                        UUID.randomUUID(),
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
}