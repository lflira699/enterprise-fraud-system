CREATE TABLE integration.integration_webhook (
    webhook_id UUID NOT NULL DEFAULT uuidv7(),
    endpoint_id UUID NOT NULL,
    event_name VARCHAR(100) NOT NULL,
    target_url VARCHAR(500) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    last_execution TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_integration_webhook
        PRIMARY KEY (webhook_id),

    CONSTRAINT fk_integration_webhook_endpoint
        FOREIGN KEY (endpoint_id)
        REFERENCES integration.integration_endpoint (endpoint_id)
);

CREATE INDEX idx_webhook_event
    ON integration.integration_webhook (event_name);

CREATE INDEX idx_webhook_status
    ON integration.integration_webhook (status);

CREATE INDEX idx_webhook_endpoint
    ON integration.integration_webhook (endpoint_id);