-- EFS-DB-002
-- V21 - Transaction Event

CREATE TABLE transaction.transaction_event (
    event_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    event_type VARCHAR(60) NOT NULL,
    event_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    component_name VARCHAR(100) NOT NULL,
    event_result VARCHAR(30),
    severity VARCHAR(20),
    correlation_id UUID,
    request_id UUID,
    event_message TEXT,
    execution_time_ms INTEGER,

    CONSTRAINT pk_transaction_event
        PRIMARY KEY (event_id),

    CONSTRAINT fk_transaction_event_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_event_transaction
    ON transaction.transaction_event (transaction_id);

CREATE INDEX idx_event_type
    ON transaction.transaction_event (event_type);

CREATE INDEX idx_event_timestamp
    ON transaction.transaction_event (event_timestamp);

CREATE INDEX idx_event_component
    ON transaction.transaction_event (component_name);

CREATE INDEX idx_event_correlation
    ON transaction.transaction_event (correlation_id);