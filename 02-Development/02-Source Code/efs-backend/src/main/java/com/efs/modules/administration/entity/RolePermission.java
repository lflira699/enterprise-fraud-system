package com.efs.modules.administration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

@Entity
@Table(name = "role_permission", schema = "administration")
public class RolePermission {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(
            name = "role_permission_id",
            nullable = false,
            updatable = false
    )
    private UUID rolePermissionId;

    @Column(
            name = "role_id",
            nullable = false,
            updatable = false
    )
    private UUID roleId;

    @Column(
            name = "permission_id",
            nullable = false,
            updatable = false
    )
    private UUID permissionId;

    @Generated
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(
            name = "granted_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime grantedAt;

    @Column(
            name = "granted_by",
            updatable = false
    )
    private UUID grantedBy;

    public UUID getRolePermissionId() {
        return rolePermissionId;
    }

    public void setRolePermissionId(
            UUID rolePermissionId) {

        this.rolePermissionId =
                rolePermissionId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(
            UUID roleId) {

        this.roleId =
                roleId;
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(
            UUID permissionId) {

        this.permissionId =
                permissionId;
    }

    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(
            LocalDateTime grantedAt) {

        this.grantedAt =
                grantedAt;
    }

    public UUID getGrantedBy() {
        return grantedBy;
    }

    public void setGrantedBy(
            UUID grantedBy) {

        this.grantedBy =
                grantedBy;
    }
}