-- EFS-DB-002
-- V67 - Audit Export
-- Controlled Physical Design Decision:
-- Completes physical typing for the approved audit_export structure.

CREATE TABLE audit.audit_export (
    export_id UUID NOT NULL DEFAULT uuidv7(),
    user_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    export_type VARCHAR(40) NOT NULL,
    resource_type VARCHAR(60) NOT NULL,
    resource_id UUID,
    file_format VARCHAR(20) NOT NULL,
    record_count BIGINT NOT NULL,
    export_reason TEXT,
    exported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_audit_export
        PRIMARY KEY (export_id),

    CONSTRAINT fk_audit_export_user
        FOREIGN KEY (user_id)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT fk_audit_export_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id)
);

CREATE INDEX idx_export_user
    ON audit.audit_export (user_id);

CREATE INDEX idx_export_resource
    ON audit.audit_export (resource_type, resource_id);

CREATE INDEX idx_export_date
    ON audit.audit_export (exported_at);

CREATE INDEX idx_export_type
    ON audit.audit_export (export_type);