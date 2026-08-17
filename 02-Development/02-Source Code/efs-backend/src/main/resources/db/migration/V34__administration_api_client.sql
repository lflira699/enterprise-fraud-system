-- EFS-DB-002
-- V34 - Administration API Client

CREATE TABLE administration.api_client (
    api_client_id UUID NOT NULL DEFAULT uuidv7(),
    organization_id UUID NOT NULL,
    client_name VARCHAR(150) NOT NULL,
    client_identifier VARCHAR(120) NOT NULL,
    authentication_type VARCHAR(40) NOT NULL,
    client_status VARCHAR(20) NOT NULL,
    allowed_scopes TEXT,
    rate_limit INTEGER,
    last_access TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_api_client
        PRIMARY KEY (api_client_id),

    CONSTRAINT fk_api_client_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id)
);

CREATE INDEX idx_api_client
    ON administration.api_client (client_identifier);

CREATE INDEX idx_api_status
    ON administration.api_client (client_status);

CREATE INDEX idx_api_organization
    ON administration.api_client (organization_id);