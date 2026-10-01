package com.efs.modules.administration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "role", schema = "administration")
public class Role {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(
            name = "role_id",
            nullable = false
    )
    private UUID roleId;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(
            name = "role_code",
            nullable = false,
            length = 60
    )
    private String roleCode;

    @Column(
            name = "role_name",
            nullable = false,
            length = 120
    )
    private String roleName;

    @Column(name = "description")
    private String description;

    @Column(
            name = "is_system",
            nullable = false
    )
    private Boolean system;

    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public Role() {
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(
            UUID roleId) {

        this.roleId =
                roleId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(
            UUID organizationId) {

        this.organizationId =
                organizationId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(
            String roleCode) {

        this.roleCode =
                roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(
            String roleName) {

        this.roleName =
                roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description =
                description;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(
            Boolean system) {

        this.system =
                system;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {

        this.status =
                status;
    }

    public void enable() {
        this.status =
                "ACTIVE";
    }

    public void disable() {
        this.status =
                "INACTIVE";
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
