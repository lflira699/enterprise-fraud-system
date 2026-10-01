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
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RolePermissionAssignmentAssignTest {

    @Test
    void assignShouldPersistAuthorizedAssignmentAndAuditSuccess() {

        Fixture fixture = fixture("role.manage");

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        UUID assignmentId = UUID.randomUUID();

        Role role = role(
                roleId,
                fixture.organizationId,
                false
        );

        Permission permission =
                permission(permissionId);

        when(fixture.roleRepository.findById(roleId))
                .thenReturn(Optional.of(role));

        when(fixture.permissionRepository.findById(permissionId))
                .thenReturn(Optional.of(permission));

        when(
                fixture.assignmentRepository
                        .existsByRoleIdAndPermissionId(
                                roleId,
                                permissionId
                        )
        ).thenReturn(false);

        when(
                fixture.assignmentRepository
                        .save(any(RolePermission.class))
        ).thenAnswer(invocation -> {

            RolePermission assignment =
                    invocation.getArgument(0);

            assignment.setRolePermissionId(
                    assignmentId
            );

            return assignment;
        });

        RolePermission created =
                fixture.service.assignPermission(
                        roleId,
                        permissionId,
                        fixture.securityContext
                );

        assertEquals(
                assignmentId,
                created.getRolePermissionId()
        );

        assertEquals(
                roleId,
                created.getRoleId()
        );

        assertEquals(
                permissionId,
                created.getPermissionId()
        );

        assertEquals(
                fixture.actorUserId,
                created.getGrantedBy()
        );

        verify(
                fixture.assignmentRepository
        ).save(
                created
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditService
                );

        assertEquals(
                "ROLE_PERMISSION_ASSIGNMENT",
                audit.getEventType()
        );

        assertEquals(
                "ROLE_PERMISSION",
                audit.getEntityType()
        );

        assertEquals(
                assignmentId,
                audit.getEntityId()
        );

        assertEquals(
                "ASSIGN",
                audit.getAction()
        );

        assertEquals(
                "ADMINISTRATION",
                audit.getSourceComponent()
        );

        assertEquals(
                "SUCCESS",
                audit.getEventResult()
        );

        assertEquals(
                roleId,
                audit.getEventDetails().get("roleId")
        );

        assertEquals(
                permissionId,
                audit.getEventDetails().get("permissionId")
        );
    }

    @Test
    void assignShouldRejectMissingManagePermissionAndAuditReason() {

        Fixture fixture = fixture();

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.assignPermission(
                                roleId,
                                permissionId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.roleRepository,
                never()
        ).findById(any(UUID.class));

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditService
                );

        assertEquals(
                "ASSIGN",
                audit.getAction()
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
    void assignShouldRejectMissingRole() {

        Fixture fixture = fixture("role.manage");

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        when(fixture.roleRepository.findById(roleId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        fixture.service.assignPermission(
                                roleId,
                                permissionId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).save(any(RolePermission.class));

        assertEquals(
                "REJECTED",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    @Test
    void assignShouldRejectRoleFromAnotherOrganization() {

        Fixture fixture = fixture("role.manage");

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        Role role = role(
                roleId,
                UUID.randomUUID(),
                false
        );

        when(fixture.roleRepository.findById(roleId))
                .thenReturn(Optional.of(role));

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.assignPermission(
                                roleId,
                                permissionId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.permissionRepository,
                never()
        ).findById(any(UUID.class));

        assertEquals(
                "REJECTED",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    @Test
    void assignShouldRejectSystemRole() {

        Fixture fixture = fixture("role.manage");

        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        Role role = role(
                roleId,
                null,
                true
        );

        when(fixture.roleRepository.findById(roleId))
                .thenReturn(Optional.of(role));

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.assignPermission(
                                roleId,
                                permissionId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).save(any(RolePermission.class));

        assertEquals(
                "REJECTED",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    @Test
    void assignShouldRejectMissingPermissionRecord() {

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
                        fixture.service.assignPermission(
                                roleId,
                                permissionId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).save(any(RolePermission.class));

        assertEquals(
                "REJECTED",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    @Test
    void assignShouldRejectDuplicateAssignment() {

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
                .thenReturn(
                        Optional.of(
                                permission(permissionId)
                        )
                );

        when(
                fixture.assignmentRepository
                        .existsByRoleIdAndPermissionId(
                                roleId,
                                permissionId
                        )
        ).thenReturn(true);

        assertThrows(
                DuplicateRecordException.class,
                () ->
                        fixture.service.assignPermission(
                                roleId,
                                permissionId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).save(any(RolePermission.class));

        assertEquals(
                "REJECTED",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    @Test
    void assignShouldAuditUnexpectedFailure() {

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
                        fixture.service.assignPermission(
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
                actorUserId,
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

    private record Fixture(
            RolePermissionAssignmentService service,
            RolePermissionRepository assignmentRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            AuditEventServiceInterface auditService,
            SecurityContext securityContext,
            UUID actorUserId,
            UUID organizationId) {
    }
}