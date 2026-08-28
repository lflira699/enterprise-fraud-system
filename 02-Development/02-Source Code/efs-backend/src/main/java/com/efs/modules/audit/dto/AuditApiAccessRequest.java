package com.efs.modules.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.net.InetAddress;
import java.util.UUID;

public class AuditApiAccessRequest {

    @NotNull
    private UUID apiClientId;

    @NotBlank
    @Size(max = 250)
    private String endpoint;

    @NotBlank
    @Size(max = 10)
    private String httpMethod;

    @NotNull
    private Integer responseCode;

    @NotNull
    private Integer executionTimeMs;

    private Long requestSize;

    private Long responseSize;

    private InetAddress ipAddress;

    private UUID correlationId;

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
}