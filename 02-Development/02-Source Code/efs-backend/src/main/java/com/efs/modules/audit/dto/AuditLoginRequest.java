package com.efs.modules.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.net.InetAddress;
import java.util.UUID;

public class AuditLoginRequest {

    private UUID userId;

    private InetAddress ipAddress;

    @Size(max = 255)
    private String deviceFingerprint;

    @NotBlank
    @Size(max = 60)
    private String authenticationMethod;

    @Size(max = 30)
    private String mfaResult;

    @NotBlank
    @Size(max = 30)
    private String loginResult;

    private String failureReason;

    @Size(max = 2)
    private String countryCode;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
}