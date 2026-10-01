package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Role;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRoleAssignmentRemoveTest {

    @Test
    void removeShouldCloseAssignmentTemporallyAndAuditSuccess() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        UUID roleId =
                UUID.randomUUID();

        UUID assignmentId =
                UUID.randomUUID();

        stubAuthorizedTargets(
                fixture,
                targetUserId,
                roleId
        );

        UserRole assignment =
                assignment(
                        assignmentId,
                        targetUserId,
                        roleId
                );

        when(
                fixture.userRoleRepository.findActiveAssignment(
                        any(UUID.class),
                        any(UUID.class),
                        any(LocalDateTime.class)
                )
        ).thenReturn(
                Optional.of(
                        assignment
                )
        );

        when(
                fixture.userRoleRepository.save(
                        assignment
                )
        ).thenReturn(
                assignment
        );

        UserRole result =
                fixture.service.removeRole(
                        targetUserId,
                        roleId,
                        fixture.securityContext
                );

        assertEquals(
                assignmentId,
                result.getUserRoleId()
        );

        assertNotNull(
                result.getEffectiveTo()
        );

        verify(
                fixture.userRoleRepository
        ).save(
                assignment
        );

        verify(
                fixture.userRoleRepository,
                never()
        ).delete(
                any(UserRole.class)
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditService
                );

        assertEquals(
                "REMOVE",
                audit.getAction()
        );

        assertEquals(
                "SUCCESS",
                audit.getEventResult()
        );

        assertEquals(
                assignmentId,
                audit.getEntityId()
        );
    }

    @Test
    void removeShouldRejectMissingManagePermission() {

        Fixture fixture =
                fixture();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.removeRole(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                fixture.securityContext
                        )
        );

        verify(
                fixture.userRoleRepository,
                never()
        ).save(
                any(UserRole.class)
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
    void removeShouldRejectMissingUser() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        UUID roleId =
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
                        fixture.service.removeRole(
                                targetUserId,
                                roleId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.roleRepository,
                never()
        ).findById(
                any(UUID.class)
        );
    }

    @Test
    void removeShouldRejectUserFromAnotherOrganization() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        UUID roleId =
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
                        fixture.service.removeRole(
                                targetUserId,
                                roleId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.roleRepository,
                never()
        ).findById(
                any(UUID.class)
        );
    }

    @Test
    void removeShouldRejectMissingRole() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        UUID roleId =
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
                fixture.roleRepository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        fixture.service.removeRole(
                                targetUserId,
                                roleId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.userRoleRepository,
                never()
        ).save(
                any(UserRole.class)
        );
    }

    @Test
    void removeShouldRejectSystemRole() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        UUID roleId =
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
                fixture.roleRepository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(
                        role(
                                roleId,
                                null,
                                true,
                                "ACTIVE"
                        )
                )
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.removeRole(
                                targetUserId,
                                roleId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.userRoleRepository,
                never()
        ).save(
                any(UserRole.class)
        );
    }

    @Test
    void removeShouldRejectRoleFromAnotherOrganization() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        UUID roleId =
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
                fixture.roleRepository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(
                        role(
                                roleId,
                                UUID.randomUUID(),
                                false,
                                "ACTIVE"
                        )
                )
        );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.removeRole(
                                targetUserId,
                                roleId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.userRoleRepository,
                never()
        ).findActiveAssignment(
                any(UUID.class),
                any(UUID.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    void removeShouldRejectMissingActiveAssignment() {

        Fixture fixture =
                fixture("user.manage");

        UUID targetUserId =
                UUID.randomUUID();

        UUID roleId =
                UUID.randomUUID();

        stubAuthorizedTargets(
                fixture,
                targetUserId,
                roleId
        );

        when(
                fixture.userRoleRepository.findActiveAssignment(
                        any(UUID.class),
                        any(UUID.class),
                        any(LocalDateTime.class)
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        fixture.service.removeRole(
                                targetUserId,
                                roleId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.userRoleRepository,
                never()
        ).save(
                any(UserRole.class)
        );

        assertEquals(
                "REJECTED",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    private static void stubAuthorizedTargets(
            Fixture fixture,
            UUID targetUserId,
            UUID roleId) {

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
                fixture.roleRepository.findById(
                        roleId
                )
        ).thenReturn(
                Optional.of(
                        role(
                                roleId,
                                fixture.organizationId,
                                false,
                                "ACTIVE"
                        )
                )
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
                roleRepository,
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

    private static Role role(
            UUID roleId,
            UUID organizationId,
            boolean system,
            String status) {

        Role role =
                new Role();

        role.setRoleId(
                roleId
        );

        role.setOrganizationId(
                organizationId
        );

        role.setSystem(
                system
        );

        role.setStatus(
                status
        );

        return role;
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

        assignment.setEffectiveFrom(
                LocalDateTime.now().minusDays(1)
        );

        assignment.setEffectiveTo(
                null
        );

        return assignment;
    }

    private record Fixture(
            UserRoleAssignmentService service,
            UserRoleRepository userRoleRepository,
            UserAccountRepository userAccountRepository,
            RoleRepository roleRepository,
            AuditEventServiceInterface auditService,
            SecurityContext securityContext,
            UUID organizationId) {
    }
}