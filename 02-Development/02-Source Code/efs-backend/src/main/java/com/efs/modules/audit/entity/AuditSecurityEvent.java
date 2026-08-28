package com.efs.modules.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_security_event", schema = "audit")
public class AuditSecurityEvent {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "security_event_id", nullable = false)
    private UUID securityEventId;

    @Column(name = "audit_event_id")
    private UUID auditEventId;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "event_category", nullable = false, length = 50)
    private String eventCategory;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    @Column(name = "source_ip")
    private InetAddress sourceIp;

    @Column(name = "affected_resource", length = 100)
    private String affectedResource;

    @Column(name = "mitigation_action", length = 100)
    private String mitigationAction;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    public AuditSecurityEvent() {
    }

    @PrePersist
    public void prePersist() {

        if (detectedAt == null) {
            detectedAt = LocalDateTime.now();
        }
    }

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