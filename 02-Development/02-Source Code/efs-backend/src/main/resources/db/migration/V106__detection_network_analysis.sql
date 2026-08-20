-- EFS-DB-002
-- V106 - Detection Network Analysis

CREATE TABLE detection.network_analysis (
    network_analysis_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID,
    transaction_id UUID,
    correlation_id UUID,
    analysis_status VARCHAR(30) NOT NULL,
    network_type VARCHAR(40) NOT NULL,
    network_key VARCHAR(120),
    entity_count INTEGER NOT NULL DEFAULT 0,
    relationship_count INTEGER NOT NULL DEFAULT 0,
    network_confidence NUMERIC(8,4),
    network_indicators JSONB,
    analysis_context JSONB,
    analyzed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_network_analysis
        PRIMARY KEY (network_analysis_id),

    CONSTRAINT fk_detection_network_analysis_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT fk_detection_network_analysis_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_detection_network_analysis_correlation
        FOREIGN KEY (correlation_id)
        REFERENCES detection.correlation (correlation_id)
);

CREATE INDEX idx_detection_network_analysis_customer
    ON detection.network_analysis (customer_id);

CREATE INDEX idx_detection_network_analysis_transaction
    ON detection.network_analysis (transaction_id);

CREATE INDEX idx_detection_network_analysis_correlation
    ON detection.network_analysis (correlation_id);

CREATE INDEX idx_detection_network_analysis_type
    ON detection.network_analysis (network_type);

CREATE INDEX idx_detection_network_analysis_status
    ON detection.network_analysis (analysis_status);

CREATE INDEX idx_detection_network_analysis_key
    ON detection.network_analysis (network_key);

CREATE INDEX idx_detection_network_analysis_date
    ON detection.network_analysis (analyzed_at);

CREATE INDEX gin_detection_network_analysis_indicators
    ON detection.network_analysis USING GIN (network_indicators);

CREATE INDEX gin_detection_network_analysis_context
    ON detection.network_analysis USING GIN (analysis_context);