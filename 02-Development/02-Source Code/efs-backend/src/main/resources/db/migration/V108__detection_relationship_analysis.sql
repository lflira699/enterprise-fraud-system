-- EFS-DB-002
-- V108 - Detection Relationship Analysis

CREATE TABLE detection.relationship_analysis (
    relationship_analysis_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID,
    transaction_id UUID,
    correlation_id UUID,
    analysis_status VARCHAR(30) NOT NULL,
    relationship_type VARCHAR(50) NOT NULL,
    source_entity_type VARCHAR(40) NOT NULL,
    source_entity_key VARCHAR(180) NOT NULL,
    target_entity_type VARCHAR(40) NOT NULL,
    target_entity_key VARCHAR(180) NOT NULL,
    relationship_strength NUMERIC(8,4),
    entity_count INTEGER NOT NULL DEFAULT 0,
    relationship_count INTEGER NOT NULL DEFAULT 0,
    relationship_indicators JSONB,
    analysis_context JSONB,
    analyzed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_relationship_analysis
        PRIMARY KEY (relationship_analysis_id),

    CONSTRAINT fk_detection_relationship_analysis_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT fk_detection_relationship_analysis_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_detection_relationship_analysis_correlation
        FOREIGN KEY (correlation_id)
        REFERENCES detection.correlation (correlation_id)
);

CREATE INDEX idx_detection_relationship_analysis_customer
    ON detection.relationship_analysis (customer_id);

CREATE INDEX idx_detection_relationship_analysis_transaction
    ON detection.relationship_analysis (transaction_id);

CREATE INDEX idx_detection_relationship_analysis_correlation
    ON detection.relationship_analysis (correlation_id);

CREATE INDEX idx_detection_relationship_analysis_type
    ON detection.relationship_analysis (relationship_type);

CREATE INDEX idx_detection_relationship_analysis_source
    ON detection.relationship_analysis (
        source_entity_type,
        source_entity_key
    );

CREATE INDEX idx_detection_relationship_analysis_target
    ON detection.relationship_analysis (
        target_entity_type,
        target_entity_key
    );

CREATE INDEX idx_detection_relationship_analysis_status
    ON detection.relationship_analysis (analysis_status);

CREATE INDEX idx_detection_relationship_analysis_date
    ON detection.relationship_analysis (analyzed_at);

CREATE INDEX gin_detection_relationship_analysis_indicators
    ON detection.relationship_analysis
    USING GIN (relationship_indicators);

CREATE INDEX gin_detection_relationship_analysis_context
    ON detection.relationship_analysis
    USING GIN (analysis_context);