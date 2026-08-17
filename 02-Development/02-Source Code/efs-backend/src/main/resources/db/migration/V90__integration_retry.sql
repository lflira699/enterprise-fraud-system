CREATE TABLE integration.integration_retry (
    retry_id UUID NOT NULL DEFAULT uuidv7(),
    message_id UUID NOT NULL,
    attempt_number INTEGER NOT NULL,
    error_message TEXT,
    next_attempt TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_integration_retry
        PRIMARY KEY (retry_id),

    CONSTRAINT fk_integration_retry_message
        FOREIGN KEY (message_id)
        REFERENCES integration.integration_message (message_id)
);

CREATE INDEX idx_retry_message
    ON integration.integration_retry (message_id);

CREATE INDEX idx_retry_status
    ON integration.integration_retry (status);

CREATE INDEX idx_retry_next_attempt
    ON integration.integration_retry (next_attempt);