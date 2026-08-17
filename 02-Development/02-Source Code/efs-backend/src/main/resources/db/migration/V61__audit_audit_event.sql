-- EFS-DB-002
-- V61 - Audit Event

CREATE TABLE audit.audit_event (
    audit_event_id UUID NOT NULL DEFAULT uuidv7(),
    event_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    organization_id UUID,
    tenant_id UUID,
    user_id UUID,
    session_id UUID,
    event_type VARCHAR(60) NOT NULL,
    entity_type VARCHAR(60),
    entity_id UUID,
    action VARCHAR(40) NOT NULL,
    source_component VARCHAR(100) NOT NULL,
    ip_address INET,
    correlation_id UUID,
    event_result VARCHAR(20) NOT NULL,
    event_details JSONB,

    CONSTRAINT pk_audit_event
        PRIMARY KEY (audit_event_id),

    CONSTRAINT fk_audit_event_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_audit_event_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id),

    CONSTRAINT fk_audit_event_user
        FOREIGN KEY (user_id)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT fk_audit_event_session
        FOREIGN KEY (session_id)
        REFERENCES administration.user_session (session_id)
);

CREATE INDEX idx_audit_event_date
    ON audit.audit_event (event_timestamp);

CREATE INDEX idx_audit_event_user
    ON audit.audit_event (user_id);

CREATE INDEX idx_audit_event_type
    ON audit.audit_event (event_type);

CREATE INDEX idx_audit_event_entity
    ON audit.audit_event (entity_type, entity_id);

CREATE INDEX idx_audit_event_correlation
    ON audit.audit_event (correlation_id);

CREATE INDEX gin_audit_event_details
    ON audit.audit_event USING GIN (event_details);