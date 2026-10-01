package com.efs.modules.administration.repository;

import com.efs.modules.administration.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository
        extends JpaRepository<Role, UUID> {

    Optional<Role> findByRoleIdAndOrganizationId(
            UUID roleId,
            UUID organizationId
    );

    boolean existsByRoleCode(
            String roleCode
    );

    boolean existsByRoleNameAndOrganizationId(
            String roleName,
            UUID organizationId
    );

    boolean existsByRoleCodeAndRoleIdNot(
            String roleCode,
            UUID roleId
    );

    boolean existsByRoleNameAndOrganizationIdAndRoleIdNot(
            String roleName,
            UUID organizationId,
            UUID roleId
    );
}
