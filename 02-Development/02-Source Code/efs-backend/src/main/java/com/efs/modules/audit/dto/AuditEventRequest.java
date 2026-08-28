package com.efs.modules.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

public class AuditEventRequest {

    private UUID organizationId;

    private UUID tenantId;

    private UUID userId;

    private UUID sessionId;

    @NotBlank
    @Size(max = 60)
    private String eventType;

    @Size(max = 60)
    private String entityType;

    private UUID entityId;

    @NotBlank
    @Size(max = 40)
    private String action;

    @NotBlank
    @Size(max = 100)
    private String sourceComponent;

    private InetAddress ipAddress;

    private UUID correlationId;

    @NotBlank
    @Size(max = 20)
    private String eventResult;

    private Map<String, Object> eventDetails;

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSourceComponent() {
        return sourceComponent;
    }

    public void setSourceComponent(String sourceComponent) {
        this.sourceComponent = sourceComponent;
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

    public String getEventResult() {
        return eventResult;
    }

    public void setEventResult(String eventResult) {
        this.eventResult = eventResult;
    }

    public Map<String, Object> getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(Map<String, Object> eventDetails) {
        this.eventDetails = eventDetails;
    }
}