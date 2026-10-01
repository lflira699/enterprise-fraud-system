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
@Table(name = "permission", schema = "administration")
public class Permission {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(
            name = "permission_id",
            nullable = false,
            updatable = false
    )
    private UUID permissionId;

    @Column(
            name = "permission_code",
            nullable = false,
            length = 80,
            updatable = false
    )
    private String permissionCode;

    @Column(
            name = "permission_name",
            nullable = false,
            length = 150
    )
    private String permissionName;

    @Column(
            name = "resource",
            nullable = false,
            length = 80,
            updatable = false
    )
    private String resource;

    @Column(
            name = "action",
            nullable = false,
            length = 60,
            updatable = false
    )
    private String action;

    @Column(name = "description")
    private String description;

    @Generated
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    public UUID getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(
            UUID permissionId) {
        this.permissionId =
                permissionId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(
            String permissionCode) {
        this.permissionCode =
                permissionCode;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(
            String permissionName) {
        this.permissionName =
                permissionName;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(
            String resource) {
        this.resource =
                resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(
            String action) {
        this.action =
                action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description =
                description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {
        this.createdAt =
                createdAt;
    }
}