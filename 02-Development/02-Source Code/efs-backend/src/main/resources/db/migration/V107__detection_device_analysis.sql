-- EFS-DB-002
-- V107 - Detection Device Analysis

CREATE TABLE detection.device_analysis (
    device_analysis_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID,
    transaction_id UUID,
    correlation_id UUID,
    analysis_status VARCHAR(30) NOT NULL,
    device_id VARCHAR(120),
    device_fingerprint VARCHAR(180),
    device_type VARCHAR(40),
    operating_system VARCHAR(80),
    browser VARCHAR(120),
    ip_address VARCHAR(64),
    geolocation_context JSONB,
    device_indicators JSONB,
    analysis_context JSONB,
    device_confidence NUMERIC(8,4),
    analyzed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_device_analysis
        PRIMARY KEY (device_analysis_id),

    CONSTRAINT fk_detection_device_analysis_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT fk_detection_device_analysis_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_detection_device_analysis_correlation
        FOREIGN KEY (correlation_id)
        REFERENCES detection.correlation (correlation_id)
);

CREATE INDEX idx_detection_device_analysis_customer
    ON detection.device_analysis (customer_id);

CREATE INDEX idx_detection_device_analysis_transaction
    ON detection.device_analysis (transaction_id);

CREATE INDEX idx_detection_device_analysis_correlation
    ON detection.device_analysis (correlation_id);

CREATE INDEX idx_detection_device_analysis_device
    ON detection.device_analysis (device_id);

CREATE INDEX idx_detection_device_analysis_fingerprint
    ON detection.device_analysis (device_fingerprint);

CREATE INDEX idx_detection_device_analysis_ip
    ON detection.device_analysis (ip_address);

CREATE INDEX idx_detection_device_analysis_status
    ON detection.device_analysis (analysis_status);

CREATE INDEX idx_detection_device_analysis_date
    ON detection.device_analysis (analyzed_at);

CREATE INDEX gin_detection_device_analysis_geolocation
    ON detection.device_analysis USING GIN (geolocation_context);

CREATE INDEX gin_detection_device_analysis_indicators
    ON detection.device_analysis USING GIN (device_indicators);

CREATE INDEX gin_detection_device_analysis_context
    ON detection.device_analysis USING GIN (analysis_context);