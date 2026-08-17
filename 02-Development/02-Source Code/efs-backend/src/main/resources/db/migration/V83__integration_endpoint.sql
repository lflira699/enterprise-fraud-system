-- EFS-DB-002
-- V83 - Integration Endpoint

CREATE TABLE integration.integration_endpoint (
    endpoint_id UUID NOT NULL DEFAULT uuidv7(),
    endpoint_code VARCHAR(60) NOT NULL,
    endpoint_name VARCHAR(150) NOT NULL,
    endpoint_url VARCHAR(500) NOT NULL,
    protocol VARCHAR(20) NOT NULL,
    authentication_type VARCHAR(30) NOT NULL,
    timeout_seconds INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_integration_endpoint
        PRIMARY KEY (endpoint_id),

    CONSTRAINT uk_integration_endpoint_code
        UNIQUE (endpoint_code)
);

CREATE INDEX idx_endpoint_code
    ON integration.integration_endpoint (endpoint_code);

CREATE INDEX idx_endpoint_protocol
    ON integration.integration_endpoint (protocol);

CREATE INDEX idx_endpoint_status
    ON integration.integration_endpoint (status);