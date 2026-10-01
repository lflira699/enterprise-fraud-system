package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.entity.Role;
import com.efs.modules.administration.entity.RolePermission;
import com.efs.modules.administration.repository.PermissionRepository;
import com.efs.modules.administration.repository.RolePermissionRepository;
import com.efs.modules.administration.repository.RoleRepository;
import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RolePermissionAssignmentViewTest {

    @Test
    void viewShouldReturnAssignmentsAndAuditSuccess() {

        Fixture fixture = fixture("role.view");

        UUID roleId = UUID.randomUUID();

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

        RolePermission first =
                assignment(
                        UUID.randomUUID(),
                        roleId,
                        UUID.randomUUID()
                );

        RolePermission second =
                assignment(
                        UUID.randomUUID(),
                        roleId,
                        UUID.randomUUID()
                );

        when(
                fixture.assignmentRepository.findByRoleId(
                        roleId
                )
        ).thenReturn(
                List.of(
                        first,
                        second
                )
        );

        List<RolePermission> result =
                fixture.service.getRolePermissions(
                        roleId,
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
                "ROLE_PERMISSION_ASSIGNMENT",
                audit.getEventType()
        );

        assertEquals(
                "ROLE_PERMISSION",
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
    void viewShouldReturnEmptyListAndAuditSuccess() {

        Fixture fixture = fixture("role.view");

        UUID roleId = UUID.randomUUID();

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

        when(
                fixture.assignmentRepository.findByRoleId(
                        roleId
                )
        ).thenReturn(
                List.of()
        );

        List<RolePermission> result =
                fixture.service.getRolePermissions(
                        roleId,
                        fixture.securityContext
                );

        assertTrue(
                result.isEmpty()
        );

        AuditEventRequest audit =
                captureAudit(
                        fixture.auditService
                );

        assertEquals(
                "SUCCESS",
                audit.getEventResult()
        );

        assertEquals(
                0,
                audit.getEventDetails().get("resultCount")
        );
    }

    @Test
    void viewShouldRejectMissingViewPermissionAndAuditReason() {

        Fixture fixture = fixture();

        assertThrows(
                AccessDeniedException.class,
                () ->
                        fixture.service.getRolePermissions(
                                UUID.randomUUID(),
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
                "REJECTED",
                audit.getEventResult()
        );

        assertEquals(
                "MISSING_PERMISSION",
                audit.getEventDetails().get("reason")
        );
    }

    @Test
    void viewShouldRejectNullRoleId() {

        Fixture fixture = fixture("role.view");

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        fixture.service.getRolePermissions(
                                null,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).findByRoleId(any(UUID.class));

        assertEquals(
                "REJECTED",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    @Test
    void viewShouldRejectMissingRole() {

        Fixture fixture = fixture("role.view");

        UUID roleId = UUID.randomUUID();

        when(fixture.roleRepository.findById(roleId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        fixture.service.getRolePermissions(
                                roleId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).findByRoleId(roleId);

        assertEquals(
                "REJECTED",
                captureAudit(
                        fixture.auditService
                ).getEventResult()
        );
    }

    @Test
    void viewShouldRejectRoleFromAnotherOrganization() {

        Fixture fixture = fixture("role.view");

        UUID roleId = UUID.randomUUID();

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
                        fixture.service.getRolePermissions(
                                roleId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).findByRoleId(roleId);
    }

    @Test
    void viewShouldRejectSystemRole() {

        Fixture fixture = fixture("role.view");

        UUID roleId = UUID.randomUUID();

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
                        fixture.service.getRolePermissions(
                                roleId,
                                fixture.securityContext
                        )
        );

        verify(
                fixture.assignmentRepository,
                never()
        ).findByRoleId(roleId);
    }

    @Test
    void viewShouldAuditUnexpectedFailure() {

        Fixture fixture = fixture("role.view");

        UUID roleId = UUID.randomUUID();

        when(fixture.roleRepository.findById(roleId))
                .thenThrow(
                        new IllegalStateException(
                                "repository failure"
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () ->
                        fixture.service.getRolePermissions(
                                roleId,
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
            AuditEventServiceInterface auditService,
            SecurityContext securityContext,
            UUID organizationId) {
    }
}