-- EFS-DB-002
-- V66 - Audit Configuration Change
-- Controlled Physical Design Decision:
-- Completes physical typing for the approved audit_configuration_change structure.

CREATE TABLE audit.audit_configuration_change (
    configuration_change_id UUID NOT NULL DEFAULT uuidv7(),
    audit_event_id UUID NOT NULL,
    configuration_key VARCHAR(150) NOT NULL,
    previous_value JSONB,
    current_value JSONB,
    changed_by UUID NOT NULL,
    change_reason TEXT,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_audit_configuration_change
        PRIMARY KEY (configuration_change_id),

    CONSTRAINT fk_audit_configuration_change_event
        FOREIGN KEY (audit_event_id)
        REFERENCES audit.audit_event (audit_event_id),

    CONSTRAINT fk_audit_configuration_change_user
        FOREIGN KEY (changed_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_configuration_change_key
    ON audit.audit_configuration_change (configuration_key);

CREATE INDEX idx_configuration_change_date
    ON audit.audit_configuration_change (changed_at);

CREATE INDEX idx_configuration_change_user
    ON audit.audit_configuration_change (changed_by);

CREATE INDEX gin_configuration_previous
    ON audit.audit_configuration_change USING GIN (previous_value);

CREATE INDEX gin_configuration_current
    ON audit.audit_configuration_change USING GIN (current_value);