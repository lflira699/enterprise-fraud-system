package com.efs.modules.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.net.InetAddress;
import java.util.UUID;

public class AuditSecurityEventRequest {

    private UUID auditEventId;

    private UUID organizationId;

    private UUID userId;

    @NotBlank
    @Size(max = 50)
    private String eventCategory;

    @NotBlank
    @Size(max = 20)
    private String severity;

    private InetAddress sourceIp;

    @Size(max = 100)
    private String affectedResource;

    @Size(max = 100)
    private String mitigationAction;

    public UUID getAuditEventId() {
        return auditEventId;
    }

    public void setAuditEventId(UUID auditEventId) {
        this.auditEventId = auditEventId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public InetAddress getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(InetAddress sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getAffectedResource() {
        return affectedResource;
    }

    public void setAffectedResource(String affectedResource) {
        this.affectedResource = affectedResource;
    }

    public String getMitigationAction() {
        return mitigationAction;
    }

    public void setMitigationAction(String mitigationAction) {
        this.mitigationAction = mitigationAction;
    }
}