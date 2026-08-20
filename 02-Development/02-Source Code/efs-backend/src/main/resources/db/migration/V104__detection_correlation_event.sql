-- EFS-DB-002
-- V104 - Detection Correlation Event

CREATE TABLE detection.correlation_event (
    correlation_event_id UUID NOT NULL DEFAULT uuidv7(),
    correlation_id UUID NOT NULL,
    event_id UUID NOT NULL,
    event_role VARCHAR(40),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_correlation_event
        PRIMARY KEY (correlation_event_id),

    CONSTRAINT fk_detection_correlation_event_correlation
        FOREIGN KEY (correlation_id)
        REFERENCES detection.correlation (correlation_id),

    CONSTRAINT fk_detection_correlation_event_event
        FOREIGN KEY (event_id)
        REFERENCES transaction.transaction_event (event_id),

    CONSTRAINT uk_detection_correlation_event
        UNIQUE (correlation_id, event_id)
);

CREATE INDEX idx_detection_correlation_event_correlation
    ON detection.correlation_event (correlation_id);

CREATE INDEX idx_detection_correlation_event_event
    ON detection.correlation_event (event_id);

CREATE INDEX idx_detection_correlation_event_role
    ON detection.correlation_event (event_role);