-- EFS-DB-002
-- V62 - Audit Entity Change

CREATE TABLE audit.audit_entity_change (
    change_id UUID NOT NULL DEFAULT uuidv7(),
    audit_event_id UUID NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    operation VARCHAR(20) NOT NULL,
    previous_value JSONB,
    current_value JSONB,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_audit_entity_change
        PRIMARY KEY (change_id),

    CONSTRAINT fk_audit_entity_change_event
        FOREIGN KEY (audit_event_id)
        REFERENCES audit.audit_event (audit_event_id)
);

CREATE INDEX idx_entity_change_entity
    ON audit.audit_entity_change (entity_type, entity_id);

CREATE INDEX idx_entity_change_date
    ON audit.audit_entity_change (changed_at);

CREATE INDEX gin_previous_value
    ON audit.audit_entity_change USING GIN (previous_value);

CREATE INDEX gin_current_value
    ON audit.audit_entity_change USING GIN (current_value);