package com.efs.modules.administration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_role",
        schema = "administration"
)
public class UserRole {

    @Id
    @Column(
            name = "user_role_id",
            nullable = false
    )
    @ColumnDefault("uuidv7()")
    private UUID userRoleId;

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "role_id",
            nullable = false
    )
    private UUID roleId;

    @Column(
            name = "effective_from",
            nullable = false
    )
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime effectiveFrom;

    @Column(
            name = "effective_to"
    )
    private LocalDateTime effectiveTo;

    @Column(
            name = "assigned_by"
    )
    private UUID assignedBy;

    @Column(
            name = "assigned_at",
            nullable = false
    )
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime assignedAt;

    public UserRole() {
    }

    public UUID getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(
            UUID userRoleId) {

        this.userRoleId =
                userRoleId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(
            UUID userId) {

        this.userId =
                userId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(
            UUID roleId) {

        this.roleId =
                roleId;
    }

    public LocalDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(
            LocalDateTime effectiveFrom) {

        this.effectiveFrom =
                effectiveFrom;
    }

    public LocalDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(
            LocalDateTime effectiveTo) {

        this.effectiveTo =
                effectiveTo;
    }

    public UUID getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(
            UUID assignedBy) {

        this.assignedBy =
                assignedBy;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(
            LocalDateTime assignedAt) {

        this.assignedAt =
                assignedAt;
    }
}