package com.efs.modules.administration.repository;

import com.efs.modules.administration.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepository
        extends JpaRepository<UserRole, UUID> {

    @Query("""
            select ur
            from UserRole ur
            where ur.userId = :userId
              and ur.roleId = :roleId
              and ur.effectiveFrom <= :asOf
              and (
                    ur.effectiveTo is null
                    or ur.effectiveTo > :asOf
                  )
            """)
    Optional<UserRole> findActiveAssignment(
            @Param("userId")
            UUID userId,
            @Param("roleId")
            UUID roleId,
            @Param("asOf")
            LocalDateTime asOf
    );

    @Query("""
            select ur
            from UserRole ur
            where ur.userId = :userId
              and ur.effectiveFrom <= :asOf
              and (
                    ur.effectiveTo is null
                    or ur.effectiveTo > :asOf
                  )
            order by ur.assignedAt asc
            """)
    List<UserRole> findActiveByUserId(
            @Param("userId")
            UUID userId,
            @Param("asOf")
            LocalDateTime asOf
    );

    @Query("""
            select distinct r.roleCode
            from UserRole ur,
                 Role r
            where ur.userId = :userId
              and ur.roleId = r.roleId
              and r.organizationId = :organizationId
              and ur.effectiveFrom <= :asOf
              and (
                    ur.effectiveTo is null
                    or ur.effectiveTo > :asOf
                  )
              and r.status = 'ACTIVE'
            order by r.roleCode
            """)
    List<String> findActiveRoleCodesByUserId(
            @Param("userId")
            UUID userId,
            @Param("organizationId")
            UUID organizationId,
            @Param("asOf")
            LocalDateTime asOf
    );

    @Query("""
            select distinct p.permissionCode
            from UserRole ur,
                 Role r,
                 RolePermission rp,
                 Permission p
            where ur.userId = :userId
              and ur.roleId = r.roleId
              and r.organizationId = :organizationId
              and r.roleId = rp.roleId
              and rp.permissionId = p.permissionId
              and ur.effectiveFrom <= :asOf
              and (
                    ur.effectiveTo is null
                    or ur.effectiveTo > :asOf
                  )
              and r.status = 'ACTIVE'
            order by p.permissionCode
            """)
    List<String> findActivePermissionCodesByUserId(
            @Param("userId")
            UUID userId,
            @Param("organizationId")
            UUID organizationId,
            @Param("asOf")
            LocalDateTime asOf
    );
}
