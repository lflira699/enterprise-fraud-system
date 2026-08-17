CREATE TABLE integration.integration_history (
    history_id UUID NOT NULL DEFAULT uuidv7(),
    message_id UUID NOT NULL,
    connector_id UUID NOT NULL,
    correlation_id UUID NOT NULL,
    request_id UUID NOT NULL,
    message_type VARCHAR(50) NOT NULL,
    source_system VARCHAR(100) NOT NULL,
    target_system VARCHAR(100) NOT NULL,
    payload_json JSONB NOT NULL,
    processing_time_ms INTEGER,
    message_status VARCHAR(20) NOT NULL,
    original_created_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_integration_history
        PRIMARY KEY (history_id)
);

CREATE INDEX idx_history_message
    ON integration.integration_history (message_id);

CREATE INDEX idx_history_correlation
    ON integration.integration_history (correlation_id);

CREATE INDEX idx_history_request
    ON integration.integration_history (request_id);

CREATE INDEX idx_history_archived_at
    ON integration.integration_history (archived_at);

CREATE INDEX gin_history_payload
    ON integration.integration_history
    USING GIN (payload_json);