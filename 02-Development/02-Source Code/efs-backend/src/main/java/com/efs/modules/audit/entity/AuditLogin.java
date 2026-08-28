package com.efs.modules.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_login", schema = "audit")
public class AuditLogin {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "login_id", nullable = false)
    private UUID loginId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "login_timestamp", nullable = false)
    private LocalDateTime loginTimestamp;

    @Column(name = "ip_address")
    private InetAddress ipAddress;

    @Column(name = "device_fingerprint", length = 255)
    private String deviceFingerprint;

    @Column(name = "authentication_method", nullable = false, length = 60)
    private String authenticationMethod;

    @Column(name = "mfa_result", length = 30)
    private String mfaResult;

    @Column(name = "login_result", nullable = false, length = 30)
    private String loginResult;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name = "country_code",
            length = 2,
            columnDefinition = "CHAR(2)"
    )
    private String countryCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AuditLogin() {
    }

    public UUID getLoginId() {
        return loginId;
    }

    public void setLoginId(UUID loginId) {
        this.loginId = loginId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getLoginTimestamp() {
        return loginTimestamp;
    }

    public void setLoginTimestamp(LocalDateTime loginTimestamp) {
        this.loginTimestamp = loginTimestamp;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public String getMfaResult() {
        return mfaResult;
    }

    public void setMfaResult(String mfaResult) {
        this.mfaResult = mfaResult;
    }

    public String getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(String loginResult) {
        this.loginResult = loginResult;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}