-- EFS-DB-002
-- V65 - Audit Security Event

CREATE TABLE audit.audit_security_event (
    security_event_id UUID NOT NULL DEFAULT uuidv7(),
    audit_event_id UUID,
    organization_id UUID,
    user_id UUID,
    event_category VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    source_ip INET,
    affected_resource VARCHAR(100),
    mitigation_action VARCHAR(100),
    detected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_audit_security_event
        PRIMARY KEY (security_event_id),

    CONSTRAINT fk_audit_security_event_audit_event
        FOREIGN KEY (audit_event_id)
        REFERENCES audit.audit_event (audit_event_id),

    CONSTRAINT fk_audit_security_event_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_audit_security_event_user
        FOREIGN KEY (user_id)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_security_event_user
    ON audit.audit_security_event (user_id);

CREATE INDEX idx_security_event_severity
    ON audit.audit_security_event (severity);

CREATE INDEX idx_security_event_category
    ON audit.audit_security_event (event_category);

CREATE INDEX idx_security_event_date
    ON audit.audit_security_event (detected_at);