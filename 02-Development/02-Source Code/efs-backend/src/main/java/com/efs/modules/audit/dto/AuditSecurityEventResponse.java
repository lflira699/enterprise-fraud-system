package com.efs.modules.audit.dto;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

public class AuditSecurityEventResponse {

    private UUID securityEventId;
    private UUID auditEventId;
    private UUID organizationId;
    private UUID userId;
    private String eventCategory;
    private String severity;
    private InetAddress sourceIp;
    private String affectedResource;
    private String mitigationAction;
    private LocalDateTime detectedAt;

    public UUID getSecurityEventId() {
        return securityEventId;
    }

    public void setSecurityEventId(UUID securityEventId) {
        this.securityEventId = securityEventId;
    }

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

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }
}