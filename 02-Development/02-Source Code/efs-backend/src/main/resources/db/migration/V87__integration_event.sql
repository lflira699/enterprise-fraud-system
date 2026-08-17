CREATE TABLE integration.integration_event (
    event_id UUID NOT NULL DEFAULT uuidv7(),
    event_name VARCHAR(100) NOT NULL,
    event_version VARCHAR(30) NOT NULL,
    event_payload JSONB NOT NULL,
    published_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    publisher VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,

    CONSTRAINT pk_integration_event
        PRIMARY KEY (event_id)
);

CREATE INDEX idx_event_name
    ON integration.integration_event (event_name);

CREATE INDEX idx_event_version
    ON integration.integration_event (event_version);

CREATE INDEX idx_event_date
    ON integration.integration_event (published_at);

CREATE INDEX gin_event_payload
    ON integration.integration_event
    USING GIN (event_payload);