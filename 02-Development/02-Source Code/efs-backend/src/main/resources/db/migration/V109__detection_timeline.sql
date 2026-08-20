-- EFS-DB-002
-- V109 - Detection Timeline

CREATE TABLE detection.timeline_event (
    timeline_event_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID,
    transaction_id UUID,
    correlation_id UUID,
    event_type VARCHAR(50) NOT NULL,
    event_source VARCHAR(80),
    event_reference_id UUID,
    event_timestamp TIMESTAMP NOT NULL,
    sequence_number INTEGER,
    event_summary VARCHAR(500),
    event_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_timeline_event
        PRIMARY KEY (timeline_event_id),

    CONSTRAINT fk_detection_timeline_event_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT fk_detection_timeline_event_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_detection_timeline_event_correlation
        FOREIGN KEY (correlation_id)
        REFERENCES detection.correlation (correlation_id)
);

CREATE INDEX idx_detection_timeline_event_customer
    ON detection.timeline_event (customer_id);

CREATE INDEX idx_detection_timeline_event_transaction
    ON detection.timeline_event (transaction_id);

CREATE INDEX idx_detection_timeline_event_correlation
    ON detection.timeline_event (correlation_id);

CREATE INDEX idx_detection_timeline_event_timestamp
    ON detection.timeline_event (event_timestamp);

CREATE INDEX idx_detection_timeline_event_sequence
    ON detection.timeline_event (
        correlation_id,
        sequence_number
    );

CREATE INDEX idx_detection_timeline_event_type
    ON detection.timeline_event (event_type);

CREATE INDEX gin_detection_timeline_event_data
    ON detection.timeline_event USING GIN (event_data);