package com.efs.modules.audit.dto;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLoginResponse {

    private UUID loginId;
    private UUID userId;
    private LocalDateTime loginTimestamp;
    private InetAddress ipAddress;
    private String deviceFingerprint;
    private String authenticationMethod;
    private String mfaResult;
    private String loginResult;
    private String failureReason;
    private String countryCode;
    private LocalDateTime createdAt;

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