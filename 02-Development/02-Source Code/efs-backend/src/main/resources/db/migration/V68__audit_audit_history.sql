-- EFS-DB-002
-- V68 - Audit History
-- Controlled Physical Design Decision:
-- Consolidated long-term immutable archive for Audit Aggregate records.
-- Operational audit records may be archived without coupling the historical
-- repository to the lifecycle of the source tables.

CREATE TABLE audit.audit_history (
    history_id UUID NOT NULL DEFAULT uuidv7(),
    source_table VARCHAR(80) NOT NULL,
    source_record_id UUID NOT NULL,
    organization_id UUID,
    tenant_id UUID,
    correlation_id UUID,
    event_timestamp TIMESTAMP NOT NULL,
    archived_payload JSONB NOT NULL,
    checksum_sha256 CHAR(64) NOT NULL,
    archived_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    retention_until TIMESTAMP,

    CONSTRAINT pk_audit_history
        PRIMARY KEY (history_id),

    CONSTRAINT uq_audit_history_source
        UNIQUE (source_table, source_record_id)
);

CREATE INDEX idx_audit_history_source
    ON audit.audit_history (source_table, source_record_id);

CREATE INDEX idx_audit_history_event_date
    ON audit.audit_history (event_timestamp);

CREATE INDEX idx_audit_history_organization
    ON audit.audit_history (organization_id);

CREATE INDEX idx_audit_history_tenant
    ON audit.audit_history (tenant_id);

CREATE INDEX idx_audit_history_correlation
    ON audit.audit_history (correlation_id);

CREATE INDEX idx_audit_history_archived
    ON audit.audit_history (archived_at);

CREATE INDEX idx_audit_history_retention
    ON audit.audit_history (retention_until);

CREATE INDEX gin_audit_history_payload
    ON audit.audit_history USING GIN (archived_payload);