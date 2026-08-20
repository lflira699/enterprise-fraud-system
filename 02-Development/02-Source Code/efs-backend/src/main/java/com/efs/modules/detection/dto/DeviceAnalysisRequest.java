package com.efs.modules.detection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public class DeviceAnalysisRequest {

    private UUID customerId;

    private UUID transactionId;

    private UUID correlationId;

    @NotBlank
    @Size(max = 30)
    private String analysisStatus;

    @Size(max = 120)
    private String deviceId;

    @Size(max = 180)
    private String deviceFingerprint;

    @Size(max = 40)
    private String deviceType;

    @Size(max = 80)
    private String operatingSystem;

    @Size(max = 120)
    private String browser;

    @Size(max = 64)
    private String ipAddress;

    private Map<String, Object> geolocationContext;

    private Map<String, Object> deviceIndicators;

    private Map<String, Object> analysisContext;

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public String getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(String analysisStatus) {
        this.analysisStatus = analysisStatus;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Map<String, Object> getGeolocationContext() {
        return geolocationContext;
    }

    public void setGeolocationContext(
            Map<String, Object> geolocationContext) {
        this.geolocationContext = geolocationContext;
    }

    public Map<String, Object> getDeviceIndicators() {
        return deviceIndicators;
    }

    public void setDeviceIndicators(
            Map<String, Object> deviceIndicators) {
        this.deviceIndicators = deviceIndicators;
    }

    public Map<String, Object> getAnalysisContext() {
        return analysisContext;
    }

    public void setAnalysisContext(
            Map<String, Object> analysisContext) {
        this.analysisContext = analysisContext;
    }
}