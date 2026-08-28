package com.efs.modules.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "audit_entity_change", schema = "audit")
public class AuditEntityChange {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "change_id", nullable = false)
    private UUID changeId;

    @Column(name = "audit_event_id", nullable = false)
    private UUID auditEventId;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "operation", nullable = false, length = 20)
    private String operation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "previous_value", columnDefinition = "jsonb")
    private Map<String, Object> previousValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "current_value", columnDefinition = "jsonb")
    private Map<String, Object> currentValue;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public AuditEntityChange() {
    }

    public UUID getChangeId() {
        return changeId;
    }

    public void setChangeId(UUID changeId) {
        this.changeId = changeId;
    }

    public UUID getAuditEventId() {
        return auditEventId;
    }

    public void setAuditEventId(UUID auditEventId) {
        this.auditEventId = auditEventId;
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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Map<String, Object> getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(Map<String, Object> previousValue) {
        this.previousValue = previousValue;
    }

    public Map<String, Object> getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Map<String, Object> currentValue) {
        this.currentValue = currentValue;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}