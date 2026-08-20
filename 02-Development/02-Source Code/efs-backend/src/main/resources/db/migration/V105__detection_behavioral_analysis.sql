-- EFS-DB-002
-- V105 - Detection Behavioral Analysis

CREATE TABLE detection.behavioral_analysis (
    behavioral_analysis_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    transaction_id UUID,
    correlation_id UUID,
    analysis_status VARCHAR(30) NOT NULL,
    baseline_window_days INTEGER,
    observed_window_start TIMESTAMP,
    observed_window_end TIMESTAMP,
    amount_deviation NUMERIC(12,4),
    frequency_deviation NUMERIC(12,4),
    velocity_deviation NUMERIC(12,4),
    channel_deviation NUMERIC(12,4),
    geographic_deviation NUMERIC(12,4),
    temporal_deviation NUMERIC(12,4),
    behavioral_confidence NUMERIC(8,4),
    behavioral_indicators JSONB,
    analysis_context JSONB,
    analyzed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_behavioral_analysis
        PRIMARY KEY (behavioral_analysis_id),

    CONSTRAINT fk_detection_behavioral_analysis_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT fk_detection_behavioral_analysis_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_detection_behavioral_analysis_correlation
        FOREIGN KEY (correlation_id)
        REFERENCES detection.correlation (correlation_id)
);

CREATE INDEX idx_detection_behavioral_analysis_customer
    ON detection.behavioral_analysis (customer_id);

CREATE INDEX idx_detection_behavioral_analysis_transaction
    ON detection.behavioral_analysis (transaction_id);

CREATE INDEX idx_detection_behavioral_analysis_correlation
    ON detection.behavioral_analysis (correlation_id);

CREATE INDEX idx_detection_behavioral_analysis_status
    ON detection.behavioral_analysis (analysis_status);

CREATE INDEX idx_detection_behavioral_analysis_date
    ON detection.behavioral_analysis (analyzed_at);

CREATE INDEX gin_detection_behavioral_analysis_indicators
    ON detection.behavioral_analysis USING GIN (behavioral_indicators);

CREATE INDEX gin_detection_behavioral_analysis_context
    ON detection.behavioral_analysis USING GIN (analysis_context);