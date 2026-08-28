package com.efs.modules.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_api_access", schema = "audit")
public class AuditApiAccess {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "api_access_id", nullable = false)
    private UUID apiAccessId;

    @Column(name = "api_client_id", nullable = false)
    private UUID apiClientId;

    @Column(name = "endpoint", nullable = false, length = 250)
    private String endpoint;

    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;

    @Column(name = "response_code", nullable = false)
    private Integer responseCode;

    @Column(name = "execution_time_ms", nullable = false)
    private Integer executionTimeMs;

    @Column(name = "request_size")
    private Long requestSize;

    @Column(name = "response_size")
    private Long responseSize;

    @Column(name = "ip_address")
    private InetAddress ipAddress;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    public AuditApiAccess() {
    }

    public UUID getApiAccessId() {
        return apiAccessId;
    }

    public void setApiAccessId(UUID apiAccessId) {
        this.apiAccessId = apiAccessId;
    }

    public UUID getApiClientId() {
        return apiClientId;
    }

    public void setApiClientId(UUID apiClientId) {
        this.apiClientId = apiClientId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public Integer getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Integer executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public Long getRequestSize() {
        return requestSize;
    }

    public void setRequestSize(Long requestSize) {
        this.requestSize = requestSize;
    }

    public Long getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(Long responseSize) {
        this.responseSize = responseSize;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}