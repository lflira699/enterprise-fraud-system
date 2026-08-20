-- EFS-DB-002
-- V103 - Detection Correlation

CREATE TABLE detection.correlation (
    correlation_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID,
    transaction_id UUID,
    correlation_key VARCHAR(120) NOT NULL,
    correlation_type VARCHAR(40) NOT NULL,
    correlation_status VARCHAR(30) NOT NULL,
    window_start TIMESTAMP NOT NULL,
    window_end TIMESTAMP NOT NULL,
    event_count INTEGER NOT NULL DEFAULT 0,
    matched_rule_count SMALLINT NOT NULL DEFAULT 0,
    confidence NUMERIC(8,4),
    correlation_context JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_correlation
        PRIMARY KEY (correlation_id),

    CONSTRAINT fk_detection_correlation_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT fk_detection_correlation_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_detection_correlation_customer
    ON detection.correlation (customer_id);

CREATE INDEX idx_detection_correlation_transaction
    ON detection.correlation (transaction_id);

CREATE INDEX idx_detection_correlation_key
    ON detection.correlation (correlation_key);

CREATE INDEX idx_detection_correlation_type
    ON detection.correlation (correlation_type);

CREATE INDEX idx_detection_correlation_status
    ON detection.correlation (correlation_status);

CREATE INDEX idx_detection_correlation_window
    ON detection.correlation (window_start, window_end);

CREATE INDEX idx_detection_correlation_created
    ON detection.correlation (created_at);

CREATE INDEX gin_detection_correlation_context
    ON detection.correlation USING GIN (correlation_context);