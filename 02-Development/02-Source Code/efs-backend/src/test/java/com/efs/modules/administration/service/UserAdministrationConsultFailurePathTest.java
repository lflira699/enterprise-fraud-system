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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAdministrationConsultFailurePathTest {

    @Test
    void consultShouldRejectMissingTargetUser() {

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

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getUser(
                        targetUserId,
                        viewContext(actorUserId)
                )
        );

        verify(
                lookupService
        ).getAuthorizedUser(actorUserId);

        verify(
                repository
        ).findById(targetUserId);

        verify(
                repository,
                never()
        ).save(any(UserAccount.class));
    }

    @Test
    void consultShouldRejectCrossOrganizationTarget() {

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
        UUID actorOrganizationId = UUID.randomUUID();
        UUID targetOrganizationId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        UserAccountReference actor =
                new UserAccountReference(
                        actorUserId,
                        actorOrganizationId,
                        null,
                        "actor@example.com"
                );

        UserAccount target =
                new UserAccount();

        target.setUserId(targetUserId);
        target.setOrganizationId(
                targetOrganizationId
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        assertThrows(
                AccessDeniedException.class,
                () -> service.getUser(
                        targetUserId,
                        viewContext(actorUserId)
                )
        );

        verify(
                repository,
                never()
        ).save(any(UserAccount.class));
    }

    @Test
    void consultShouldRejectCrossTenantTargetForTenantScopedActor() {

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
        UUID targetTenantId = UUID.randomUUID();
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
                targetTenantId
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        assertThrows(
                AccessDeniedException.class,
                () -> service.getUser(
                        targetUserId,
                        viewContext(actorUserId)
                )
        );

        verify(
                repository,
                never()
        ).save(any(UserAccount.class));
    }

    @Test
    void consultShouldAllowOwnTenantTargetForTenantScopedActor() {

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

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        UserAccount result =
                service.getUser(
                        targetUserId,
                        viewContext(actorUserId)
                );

        assertSame(
                target,
                result
        );

        verify(
                repository,
                never()
        ).save(any(UserAccount.class));
    }

    @Test
    void consultShouldAllowOrganizationScopedActorWithinOrganization() {

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
        UUID targetTenantId = UUID.randomUUID();
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
                targetTenantId
        );

        when(
                lookupService.getAuthorizedUser(actorUserId)
        ).thenReturn(actor);

        when(
                repository.findById(targetUserId)
        ).thenReturn(Optional.of(target));

        UserAccount result =
                service.getUser(
                        targetUserId,
                        viewContext(actorUserId)
                );

        assertSame(
                target,
                result
        );

        verify(
                repository,
                never()
        ).save(any(UserAccount.class));
    }

    private SecurityContext viewContext(
            UUID actorUserId) {

        return new SecurityContext(
                actorUserId,
                null,
                null,
                Set.of(),
                Set.of("user.view"),
                Set.of()
        );
    }

    @Test
    void consultShouldRejectMissingViewPermission() {

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
                        Set.of(),
                        Set.of()
                );

        assertThrows(
                AccessDeniedException.class,
                () -> service.getUser(
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
