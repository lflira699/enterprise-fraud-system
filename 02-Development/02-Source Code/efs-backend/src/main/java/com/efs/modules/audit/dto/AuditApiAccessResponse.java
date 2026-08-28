package com.efs.modules.audit.dto;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

public class AuditApiAccessResponse {

    private UUID apiAccessId;
    private UUID apiClientId;
    private String endpoint;
    private String httpMethod;
    private Integer responseCode;
    private Integer executionTimeMs;
    private Long requestSize;
    private Long responseSize;
    private InetAddress ipAddress;
    private UUID correlationId;
    private LocalDateTime requestedAt;

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