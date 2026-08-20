CREATE TABLE integration.outbox_event (
    id UUID NOT NULL DEFAULT uuidv7(),
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    correlation_id UUID,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attempt_count INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMP,
    published_at TIMESTAMP,
    last_error TEXT,

    CONSTRAINT pk_outbox_event
        PRIMARY KEY (id),

    CONSTRAINT chk_outbox_event_status
        CHECK (
            status IN (
                'PENDING',
                'PROCESSING',
                'PUBLISHED',
                'FAILED'
            )
        )
);

CREATE INDEX idx_outbox_event_status
    ON integration.outbox_event (status);

CREATE INDEX idx_outbox_event_next_attempt
    ON integration.outbox_event (next_attempt_at);

CREATE INDEX idx_outbox_event_event_type
    ON integration.outbox_event (event_type);

CREATE INDEX idx_outbox_event_aggregate
    ON integration.outbox_event (aggregate_type, aggregate_id);

CREATE INDEX idx_outbox_event_correlation
    ON integration.outbox_event (correlation_id);

CREATE INDEX idx_outbox_event_pending
    ON integration.outbox_event (status, next_attempt_at);

CREATE INDEX gin_outbox_event_payload
    ON integration.outbox_event
    USING GIN (payload);