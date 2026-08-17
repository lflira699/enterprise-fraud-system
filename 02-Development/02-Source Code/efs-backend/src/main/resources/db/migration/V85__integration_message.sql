CREATE TABLE integration.integration_message (
    message_id UUID NOT NULL DEFAULT uuidv7(),
    connector_id UUID NOT NULL,
    correlation_id UUID NOT NULL,
    request_id UUID NOT NULL,
    message_type VARCHAR(50) NOT NULL,
    source_system VARCHAR(100) NOT NULL,
    target_system VARCHAR(100) NOT NULL,
    payload_json JSONB NOT NULL,
    processing_time_ms INTEGER,
    message_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_integration_message
        PRIMARY KEY (message_id),

    CONSTRAINT fk_integration_message_connector
        FOREIGN KEY (connector_id)
        REFERENCES integration.integration_connector (connector_id)
);

CREATE INDEX idx_message_correlation
    ON integration.integration_message (correlation_id);

CREATE INDEX idx_message_request
    ON integration.integration_message (request_id);

CREATE INDEX idx_message_status
    ON integration.integration_message (message_status);

CREATE INDEX gin_message_payload
    ON integration.integration_message
    USING GIN (payload_json);