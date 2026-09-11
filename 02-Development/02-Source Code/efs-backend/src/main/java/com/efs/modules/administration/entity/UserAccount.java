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
@Table(name = "user_account", schema = "administration")
public class UserAccount {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "organization_id",
            nullable = false
    )
    private UUID organizationId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "business_unit_id")
    private UUID businessUnitId;

    @Column(
            name = "username",
            nullable = false,
            length = 100
    )
    private String username;

    @Column(
            name = "full_name",
            nullable = false,
            length = 200
    )
    private String fullName;

    @Column(
            name = "email",
            nullable = false,
            length = 200
    )
    private String email;

    @Column(
            name = "authentication_provider",
            nullable = false,
            length = 60
    )
    private String authenticationProvider;

    @Column(
            name = "mfa_enabled",
            nullable = false
    )
    private Boolean mfaEnabled;

    @Column(
            name = "account_status",
            nullable = false,
            length = 20
    )
    private String accountStatus;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(
            name = "failed_login_attempts",
            nullable = false
    )
    private Integer failedLoginAttempts;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public UserAccount() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(
            UUID userId) {

        this.userId =
                userId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(
            UUID organizationId) {

        this.organizationId =
                organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            UUID tenantId) {

        this.tenantId =
                tenantId;
    }

    public UUID getBusinessUnitId() {
        return businessUnitId;
    }

    public void setBusinessUnitId(
            UUID businessUnitId) {

        this.businessUnitId =
                businessUnitId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(
            String username) {

        this.username =
                username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(
            String fullName) {

        this.fullName =
                fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email) {

        this.email =
                email;
    }

    public String getAuthenticationProvider() {
        return authenticationProvider;
    }

    public void setAuthenticationProvider(
            String authenticationProvider) {

        this.authenticationProvider =
                authenticationProvider;
    }

    public Boolean getMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(
            Boolean mfaEnabled) {

        this.mfaEnabled =
                mfaEnabled;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(
            String accountStatus) {

        this.accountStatus =
                accountStatus;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(
            LocalDateTime lastLogin) {

        this.lastLogin =
                lastLogin;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(
            Integer failedLoginAttempts) {

        this.failedLoginAttempts =
                failedLoginAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(
            LocalDateTime lockedUntil) {

        this.lockedUntil =
                lockedUntil;
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