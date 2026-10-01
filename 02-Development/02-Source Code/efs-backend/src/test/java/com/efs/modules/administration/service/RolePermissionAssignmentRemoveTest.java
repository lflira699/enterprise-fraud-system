package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Permission;
import com.efs.modules.administration.entity.Role;
import com.efs.modules.administration.entity.RolePermission;
import com.efs.modules.administration.repository.PermissionRepository;
import com.efs.modules.administration.repository.RolePermissionRepository;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RolePermissionAssignmentRemoveTest {

    @Test
    void removeShouldDeleteAssignmentAndAuditSuccess() {

        Fixture fixture = fixture("role.manage");

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        UUID assignmentId = UUID.randomUUID();

        RolePermission assignment =
                assignment(
                        assignmentId,
                        roleId,
                        permissionId
                );

        stubAuthorizedTarget(
                fixture,
                roleId,
                permissionId
        );

        when(
                fixture.assignmentRepository
                        .findByRoleIdAndPermissionId(
                                roleId,
                                permissionId
                        )
        ).thenReturn(
                Optional.of(assignment)
        );

        fixture.service.removePermission(
                roleId,
                permissionId,
                fixture.securityContext
        );

        verify(
                fixture.assignmentRepository
        ).delete(
                assignment
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditService
                );

        assertEquals(
                assignmentId,
                audit.getEntityId()
        );

        assertEquals(
                "REMOVE",
                audit.getAction()
        );

        assertEquals(
                "SUCCESS",
                audit.getEventResult()
        );
    }

    @Test
    void removeShouldRejectMissingManagePermissionAndAuditReason() {

        Fixture fixture = fixture();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.removePermission(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).delete(any(RolePermission.class));

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditService
                );

        assertEquals(
                "REJECTED",
                audit.getEventResult()
        );

        assertEquals(
                "MISSING_PERMISSION",
                audit.getEventDetails().get("reason")
        );
    }

    @Test
    void removeShouldRejectMissingRole() {

        Fixture fixture = fixture("role.manage");

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        when(fixture.roleRepository.findById(roleId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        fixture.service.removePermission(
                                roleId,
                                permissionId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).delete(any(RolePermission.class));

        assertEquals(
                "REJECTED",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    @Test
    void removeShouldRejectRoleFromAnotherOrganization() {

        Fixture fixture = fixture("role.manage");

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        when(fixture.roleRepository.findById(roleId))
                .thenReturn(
                        Optional.of(
                                role(
                                        roleId,
                                        UUID.randomUUID(),
                                        false
                                )
                        )
                );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.removePermission(
                                roleId,
                                permissionId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).delete(any(RolePermission.class));
    }

    @Test
    void removeShouldRejectSystemRole() {

        Fixture fixture = fixture("role.manage");

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        when(fixture.roleRepository.findById(roleId))
                .thenReturn(
                        Optional.of(
                                role(
                                        roleId,
                                        null,
                                        true
                                )
                        )
                );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.removePermission(
                                roleId,
                                permissionId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).delete(any(RolePermission.class));
    }

    @Test
    void removeShouldRejectMissingPermissionRecord() {

        Fixture fixture = fixture("role.manage");

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        when(fixture.roleRepository.findById(roleId))
                .thenReturn(
                        Optional.of(
                                role(
                                        roleId,
                                        fixture.organizationId,
                                        false
                                )
                        )
                );

        when(fixture.permissionRepository.findById(permissionId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        fixture.service.removePermission(
                                roleId,
                                permissionId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).delete(any(RolePermission.class));
    }

    @Test
    void removeShouldRejectMissingAssignment() {

        Fixture fixture = fixture("role.manage");

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        stubAuthorizedTarget(
                fixture,
                roleId,
                permissionId
        );

        when(
                fixture.assignmentRepository
                        .findByRoleIdAndPermissionId(
                                roleId,
                                permissionId
                        )
        ).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        fixture.service.removePermission(
                                roleId,
                                permissionId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).delete(any(RolePermission.class));

        assertEquals(
                "REJECTED",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    @Test
    void removeShouldAuditUnexpectedFailure() {

        Fixture fixture = fixture("role.manage");

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        when(fixture.roleRepository.findById(roleId))
                .thenThrow(
                        new IllegalStateException(
                                "repository failure"
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () ->
                        fixture.service.removePermission(
                                roleId,
                                permissionId,
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

    private static void stubAuthorizedTarget(
            Fixture fixture,
            UUID roleId,
            UUID permissionId) {

        when(fixture.roleRepository.findById(roleId))
                .thenReturn(
                        Optional.of(
                                role(
                                        roleId,
                                        fixture.organizationId,
                                        false
                                )
                        )
                );

        when(fixture.permissionRepository.findById(permissionId))
                .thenReturn(
                        Optional.of(
                                permission(permissionId)
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

        RolePermissionRepository assignmentRepository =
                mock(RolePermissionRepository.class);

        RoleRepository roleRepository =
                mock(RoleRepository.class);

        PermissionRepository permissionRepository =
                mock(PermissionRepository.class);

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

        when(
                lookupService.getAuthorizedUser(
                        actorUserId
                )
        ).thenReturn(
                new UserAccountReference(
                        actorUserId,
                        organizationId,
                        null,
                        "actor@example.com"
                )
        );

        RolePermissionAssignmentService service =
                new RolePermissionAssignmentService(
                        assignmentRepository,
                        roleRepository,
                        permissionRepository,
                        lookupService,
                        auditService
                );

        return new Fixture(
                service,
                assignmentRepository,
                roleRepository,
                permissionRepository,
                auditService,
                securityContext,
                organizationId
        );
    }

    private static Role role(
            UUID roleId,
            UUID organizationId,
            boolean system) {

        Role role =
                new Role();

        role.setRoleId(roleId);
        role.setOrganizationId(organizationId);
        role.setSystem(system);
        role.setStatus("ACTIVE");

        return role;
    }

    private static Permission permission(
            UUID permissionId) {

        Permission permission =
                new Permission();

        permission.setPermissionId(
                permissionId
        );

        return permission;
    }

    private static RolePermission assignment(
            UUID assignmentId,
            UUID roleId,
            UUID permissionId) {

        RolePermission assignment =
                new RolePermission();

        assignment.setRolePermissionId(
                assignmentId
        );

        assignment.setRoleId(
                roleId
        );

        assignment.setPermissionId(
                permissionId
        );

        return assignment;
    }

    private record Fixture(
            RolePermissionAssignmentService service,
            RolePermissionRepository assignmentRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            AuditEventServiceInterface auditService,
            SecurityContext securityContext,
            UUID organizationId) {
    }
}