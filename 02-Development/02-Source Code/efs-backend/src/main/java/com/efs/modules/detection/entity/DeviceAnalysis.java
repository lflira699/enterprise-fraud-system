package com.efs.modules.detection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "device_analysis", schema = "detection")
public class DeviceAnalysis {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "device_analysis_id", nullable = false)
    private UUID deviceAnalysisId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "analysis_status", nullable = false, length = 30)
    private String analysisStatus;

    @Column(name = "device_id", length = 120)
    private String deviceId;

    @Column(name = "device_fingerprint", length = 180)
    private String deviceFingerprint;

    @Column(name = "device_type", length = 40)
    private String deviceType;

    @Column(name = "operating_system", length = 80)
    private String operatingSystem;

    @Column(name = "browser", length = 120)
    private String browser;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "geolocation_context", columnDefinition = "jsonb")
    private Map<String, Object> geolocationContext;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "device_indicators", columnDefinition = "jsonb")
    private Map<String, Object> deviceIndicators;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_context", columnDefinition = "jsonb")
    private Map<String, Object> analysisContext;

    @Column(name = "device_confidence", precision = 8, scale = 4)
    private BigDecimal deviceConfidence;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public DeviceAnalysis() {
    }

    public UUID getDeviceAnalysisId() {
        return deviceAnalysisId;
    }

    public void setDeviceAnalysisId(UUID deviceAnalysisId) {
        this.deviceAnalysisId = deviceAnalysisId;
    }

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

    public BigDecimal getDeviceConfidence() {
        return deviceConfidence;
    }

    public void setDeviceConfidence(BigDecimal deviceConfidence) {
        this.deviceConfidence = deviceConfidence;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}