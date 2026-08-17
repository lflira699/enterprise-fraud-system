-- EFS-DB-002
-- V64 - Audit API Access
-- Controlled Physical Design Decision:
-- Completes physical typing for the approved audit_api_access structure.

CREATE TABLE audit.audit_api_access (
    api_access_id UUID NOT NULL DEFAULT uuidv7(),
    api_client_id UUID NOT NULL,
    endpoint VARCHAR(250) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    response_code INTEGER NOT NULL,
    execution_time_ms INTEGER NOT NULL,
    request_size BIGINT,
    response_size BIGINT,
    ip_address INET,
    correlation_id UUID,
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_audit_api_access
        PRIMARY KEY (api_access_id),

    CONSTRAINT fk_audit_api_access_client
        FOREIGN KEY (api_client_id)
        REFERENCES administration.api_client (api_client_id)
);

CREATE INDEX idx_api_access_client
    ON audit.audit_api_access (api_client_id);

CREATE INDEX idx_api_access_endpoint
    ON audit.audit_api_access (endpoint);

CREATE INDEX idx_api_access_date
    ON audit.audit_api_access (requested_at);

CREATE INDEX idx_api_access_response
    ON audit.audit_api_access (response_code);