CREATE TABLE integration.integration_connector (
    connector_id UUID NOT NULL DEFAULT uuidv7(),
    endpoint_id UUID NOT NULL,
    connector_name VARCHAR(150) NOT NULL,
    connector_type VARCHAR(50) NOT NULL,
    provider VARCHAR(100) NOT NULL,
    version VARCHAR(30),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_integration_connector
        PRIMARY KEY (connector_id),

    CONSTRAINT fk_integration_connector_endpoint
        FOREIGN KEY (endpoint_id)
        REFERENCES integration.integration_endpoint (endpoint_id)
);

CREATE INDEX idx_connector_type
    ON integration.integration_connector (connector_type);

CREATE INDEX idx_connector_provider
    ON integration.integration_connector (provider);

CREATE INDEX idx_connector_status
    ON integration.integration_connector (status);
