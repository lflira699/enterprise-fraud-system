package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.UserAccount;
import com.efs.modules.administration.entity.UserRole;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.modules.administration.repository.UserAccountRepository;
import com.efs.modules.administration.repository.UserRoleRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRoleAssignmentViewTest {

    @Test
    void viewShouldReturnActiveAssignmentsAndAuditSuccess() {

        Fixture fixture =
                fixture("user.view");

        UUID targetUserId =
                UUID.randomUUID();

        when(
                fixture.userAccountRepository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.of(
                        user(
                                targetUserId,
                                fixture.organizationId
                        )
                )
        );

        UserRole first =
                assignment(
                        UUID.randomUUID(),
                        targetUserId,
                        UUID.randomUUID()
                );

        UserRole second =
                assignment(
                        UUID.randomUUID(),
                        targetUserId,
                        UUID.randomUUID()
                );

        when(
                fixture.userRoleRepository.findActiveByUserId(
                        any(UUID.class),
                        any(LocalDateTime.class)
                )
        ).thenReturn(
                List.of(
                        first,
                        second
                )
        );

        List<UserRole> result =
                fixture.service.getUserRoles(
                        targetUserId,
                        fixture.securityContext
                );

        assertEquals(
                2,
                result.size()
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditService
                );

        assertEquals(
                "USER_ROLE_ASSIGNMENT",
                audit.getEventType()
        );

        assertEquals(
                "USER_ROLE",
                audit.getEntityType()
        );

        assertEquals(
                "VIEW",
                audit.getAction()
        );

        assertEquals(
                "SUCCESS",
                audit.getEventResult()
        );

        assertEquals(
                2,
                audit.getEventDetails().get("resultCount")
        );
    }

    @Test
    void viewShouldReturnEmptyActiveAssignmentList() {

        Fixture fixture =
                fixture("user.view");

        UUID targetUserId =
                UUID.randomUUID();

        when(
                fixture.userAccountRepository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.of(
                        user(
                                targetUserId,
                                fixture.organizationId
                        )
                )
        );

        when(
                fixture.userRoleRepository.findActiveByUserId(
                        any(UUID.class),
                        any(LocalDateTime.class)
                )
        ).thenReturn(
                List.of()
        );

        List<UserRole> result =
                fixture.service.getUserRoles(
                        targetUserId,
                        fixture.securityContext
                );

        assertTrue(
                result.isEmpty()
        );

        assertEquals(
                "SUCCESS",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    @Test
    void viewShouldRejectMissingViewPermission() {

        Fixture fixture =
                fixture();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.getUserRoles(
                                UUID.randomUUID(),
                                fixture.securityContext
                        )
        );

        verify(
                fixture.userAccountRepository,
                never()
        ).findById(
                any(UUID.class)
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditService
                );

        assertEquals(
                "MISSING_PERMISSION",
                audit.getEventDetails().get("reason")
        );
    }

    @Test
    void viewShouldRejectNullTargetUserId() {

        Fixture fixture =
                fixture("user.view");

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        fixture.service.getUserRoles(
                                null,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.userAccountRepository,
                never()
        ).findById(
                any(UUID.class)
        );

        assertEquals(
                "REJECTED",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    @Test
    void viewShouldRejectMissingUser() {

        Fixture fixture =
                fixture("user.view");

        UUID targetUserId =
                UUID.randomUUID();

        when(
                fixture.userAccountRepository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        fixture.service.getUserRoles(
                                targetUserId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.userRoleRepository,
                never()
        ).findActiveByUserId(
                any(UUID.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    void viewShouldRejectUserFromAnotherOrganization() {

        Fixture fixture =
                fixture("user.view");

        UUID targetUserId =
                UUID.randomUUID();

        when(
                fixture.userAccountRepository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.of(
                        user(
                                targetUserId,
                                UUID.randomUUID()
                        )
                )
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.getUserRoles(
                                targetUserId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.userRoleRepository,
                never()
        ).findActiveByUserId(
                any(UUID.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    void viewShouldUseActiveAssignmentRepositoryPathOnly() {

        Fixture fixture =
                fixture("user.view");

        UUID targetUserId =
                UUID.randomUUID();

        when(
                fixture.userAccountRepository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.of(
                        user(
                                targetUserId,
                                fixture.organizationId
                        )
                )
        );

        when(
                fixture.userRoleRepository.findActiveByUserId(
                        any(UUID.class),
                        any(LocalDateTime.class)
                )
        ).thenReturn(
                List.of()
        );

        fixture.service.getUserRoles(
                targetUserId,
                fixture.securityContext
        );

        verify(
                fixture.userRoleRepository
        ).findActiveByUserId(
                any(UUID.class),
                any(LocalDateTime.class)
        );

        verify(
                fixture.userRoleRepository,
                never()
        ).save(
                any(UserRole.class)
        );
    }

    @Test
    void viewShouldAuditUnexpectedRepositoryFailure() {

        Fixture fixture =
                fixture("user.view");

        UUID targetUserId =
                UUID.randomUUID();

        when(
                fixture.userAccountRepository.findById(
                        targetUserId
                )
        ).thenReturn(
                Optional.of(
                        user(
                                targetUserId,
                                fixture.organizationId
                        )
                )
        );

        when(
                fixture.userRoleRepository.findActiveByUserId(
                        any(UUID.class),
                        any(LocalDateTime.class)
                )
        ).thenThrow(
                new IllegalStateException(
                        "repository failure"
                )
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        fixture.service.getUserRoles(
                                targetUserId,
                                fixture.securityContext
                        )
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditService
                );

        assertEquals(
                "FAILURE",
                audit.getEventResult()
        );

        assertEquals(
                "IllegalStateException",
                audit.getEventDetails().get("exception")
        );
    }

    private static AuditEventRequest captureAudit(
            AuditEventServiceInterface auditService) {

        ArgumentCaptor<AuditEventRequest> captor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditService
        ).createAuditEventRequiresNew(
                captor.capture()
        );

        return captor.getValue();
    }

    private static Fixture fixture(
            String... permissions) {

        UserRoleRepository userRoleRepository =
                mock(UserRoleRepository.class);

        UserAccountRepository userAccountRepository =
                mock(UserAccountRepository.class);

        RoleRepository roleRepository =
                mock(RoleRepository.class);

        UserAccountLookupServiceInterface lookupService =
                mock(UserAccountLookupServiceInterface.class);

        AuditEventServiceInterface auditService =
                mock(AuditEventServiceInterface.class);

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
                mock(UserAccountReference.class);

        when(
                actor.userId()
        ).thenReturn(
                actorUserId
        );

        when(
                actor.organizationId()
        ).thenReturn(
                organizationId
        );

        when(
                lookupService.getAuthorizedUser(
                        actorUserId
                )
        ).thenReturn(
                actor
        );

        UserRoleAssignmentService service =
                new UserRoleAssignmentService(
                        userRoleRepository,
                        userAccountRepository,
                        roleRepository,
                        lookupService,
                        auditService
                );

        return new Fixture(
                service,
                userRoleRepository,
                userAccountRepository,
                auditService,
                securityContext,
                organizationId
        );
    }

    private static UserAccount user(
            UUID userId,
            UUID organizationId) {

        UserAccount user =
                new UserAccount();

        user.setUserId(
                userId
        );

        user.setOrganizationId(
                organizationId
        );

        return user;
    }

    private static UserRole assignment(
            UUID assignmentId,
            UUID userId,
            UUID roleId) {

        UserRole assignment =
                new UserRole();

        assignment.setUserRoleId(
                assignmentId
        );

        assignment.setUserId(
                userId
        );

        assignment.setRoleId(
                roleId
        );

        return assignment;
    }

    private record Fixture(
            UserRoleAssignmentService service,
            UserRoleRepository userRoleRepository,
            UserAccountRepository userAccountRepository,
            AuditEventServiceInterface auditService,
            SecurityContext securityContext,
            UUID organizationId) {
    }
}