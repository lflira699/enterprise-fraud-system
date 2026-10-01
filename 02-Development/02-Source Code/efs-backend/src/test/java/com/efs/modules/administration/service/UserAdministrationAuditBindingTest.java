package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserAccount;
import com.efs.modules.administration.repository.UserAccountRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

class UserAdministrationAuditBindingTest {

    @Test
    void createShouldAuditSuccess() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        when(
                fixture.repository.save(
                        any(UserAccount.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        fixture.service.createUser(
                targetUserId,
                null,
                null,
                "user",
                "User",
                "user@example.com",
                "FEDERATED",
                true,
                fixture.securityContext
        );

        assertAudit(
                fixture.auditService,
                targetUserId,
                "CREATE",
                "SUCCESS"
        );
    }

    @Test
    void updateShouldAuditSuccess() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        UserAccount target =
                user(
                        targetUserId,
                        fixture.organizationId,
                        null,
                        "ACTIVE"
                );

        when(
                fixture.repository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.of(target)
        );

        when(
                fixture.repository.save(
                        any(UserAccount.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        fixture.service.updateUser(
                targetUserId,
                null,
                null,
                "updated",
                "Updated User",
                "updated@example.com",
                "FEDERATED",
                true,
                fixture.securityContext
        );

        assertAudit(
                fixture.auditService,
                targetUserId,
                "UPDATE",
                "SUCCESS"
        );
    }

    @Test
    void enableShouldAuditSuccess() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        UserAccount target =
                user(
                        targetUserId,
                        fixture.organizationId,
                        null,
                        "INACTIVE"
                );

        when(
                fixture.repository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.of(target)
        );

        when(
                fixture.repository.save(
                        any(UserAccount.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        fixture.service.enableUser(
                targetUserId,
                fixture.securityContext
        );

        assertAudit(
                fixture.auditService,
                targetUserId,
                "ENABLE",
                "SUCCESS"
        );
    }

    @Test
    void disableShouldAuditSuccess() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        UserAccount target =
                user(
                        targetUserId,
                        fixture.organizationId,
                        null,
                        "ACTIVE"
                );

        when(
                fixture.repository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.of(target)
        );

        when(
                fixture.repository.save(
                        any(UserAccount.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        fixture.service.disableUser(
                targetUserId,
                fixture.securityContext
        );

        assertAudit(
                fixture.auditService,
                targetUserId,
                "DISABLE",
                "SUCCESS"
        );
    }

    @Test
    void consultShouldAuditSuccess() {

        Fixture fixture =
                fixture("user.view");

        UUID targetUserId =
                UUID.randomUUID();

        UserAccount target =
                user(
                        targetUserId,
                        fixture.organizationId,
                        null,
                        "ACTIVE"
                );

        when(
                fixture.repository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.of(target)
        );

        fixture.service.getUser(
                targetUserId,
                fixture.securityContext
        );

        assertAudit(
                fixture.auditService,
                targetUserId,
                "VIEW",
                "SUCCESS"
        );
    }

    @Test
    void missingPermissionShouldAuditRejected() {

        Fixture fixture =
                fixture();

        UUID targetUserId =
                UUID.randomUUID();

        assertThrows(
                AccessDeniedException.class,
                () -> fixture.service.getUser(
                        targetUserId,
                        fixture.securityContext
                )
        );

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                fixture.auditService
        ).createAuditEventRequiresNew(
                captor.capture()
        );

        AuditEventRequest request =
                captor.getValue();

        assertEquals(
                "USER_ADMINISTRATION",
                request.getEventType()
        );

        assertEquals(
                "USER_ACCOUNT",
                request.getEntityType()
        );

        assertEquals(
                targetUserId,
                request.getEntityId()
        );

        assertEquals(
                "VIEW",
                request.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                request.getSourceComponent()
        );

        assertEquals(
                "REJECTED",
                request.getEventResult()
        );

        assertEquals(
                "MISSING_PERMISSION",
                request.getEventDetails().get(
                        "reason"
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
    void unexpectedFailureShouldAuditFailure() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        when(
                fixture.repository.save(
                        any(UserAccount.class)
                )
        ).thenThrow(
                new IllegalStateException(
                        "forced failure"
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> fixture.service.createUser(
                        targetUserId,
                        null,
                        null,
                        "user",
                        "User",
                        "user@example.com",
                        "FEDERATED",
                        true,
                        fixture.securityContext
                )
        );

        assertAudit(
                fixture.auditService,
                targetUserId,
                "CREATE",
                "FAILURE"
        );
    }

    private static void assertAudit(
            AuditEventServiceInterface auditService,
            UUID entityId,
            String action,
            String result) {

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditService
        ).createAuditEventRequiresNew(
                captor.capture()
        );

        AuditEventRequest request =
                captor.getValue();

        assertEquals(
                "USER_ADMINISTRATION",
                request.getEventType()
        );

        assertEquals(
                "USER_ACCOUNT",
                request.getEntityType()
        );

        assertEquals(
                entityId,
                request.getEntityId()
        );

        assertEquals(
                action,
                request.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                request.getSourceComponent()
        );

        assertEquals(
                result,
                request.getEventResult()
        );
    }

    private static UserAccount user(
            UUID userId,
            UUID organizationId,
            UUID tenantId,
            String status) {

        UserAccount user =
                new UserAccount();

        user.setUserId(userId);
        user.setOrganizationId(
                organizationId
        );
        user.setTenantId(
                tenantId
        );
        user.setAccountStatus(
                status
        );

        return user;
    }

    private static Fixture fixture(
            String... permissions) {

        UserAccountRepository repository =
                mock(UserAccountRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        TenantOrganizationLookupServiceInterface tenantLookup =
                mock(TenantOrganizationLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

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
        ).thenReturn(actor);

        UserAdministrationService service =
                new UserAdministrationService(
                        repository,
                        lookupService,
                        tenantLookup,
                        auditService
                );

        return new Fixture(
                service,
                repository,
                auditService,
                securityContext,
                organizationId
        );
    }

    private record Fixture(
            UserAdministrationService service,
            UserAccountRepository repository,
            AuditEventServiceInterface auditService,
            SecurityContext securityContext,
            UUID organizationId) {
    }
}