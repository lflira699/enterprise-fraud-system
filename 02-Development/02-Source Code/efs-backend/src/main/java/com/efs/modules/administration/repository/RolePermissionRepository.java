package com.efs.modules.administration.repository;

import com.efs.modules.administration.entity.RolePermission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository
        extends JpaRepository<RolePermission, UUID> {

    Optional<RolePermission> findByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId);

    List<RolePermission> findByRoleId(
            UUID roleId);

    boolean existsByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId);
}