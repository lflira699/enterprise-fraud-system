-- EFS-DB-002
-- V115 - Risk Assessment

CREATE TABLE transaction.risk_assessment (
    risk_assessment_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    assessment_type VARCHAR(40) NOT NULL,
    assessment_stage VARCHAR(40) NOT NULL,
    overall_risk_score NUMERIC(8,2) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    risk_category VARCHAR(40),
    assessment_result VARCHAR(40) NOT NULL,
    rules_score NUMERIC(8,2),
    machine_learning_score NUMERIC(8,2),
    behavioral_score NUMERIC(8,2),
    customer_score NUMERIC(8,2),
    geographic_score NUMERIC(8,2),
    device_score NUMERIC(8,2),
    confidence_score NUMERIC(5,2),
    model_name VARCHAR(100),
    model_version VARCHAR(40),
    assessment_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processing_time_ms BIGINT,
    assessment_details JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP,
    deleted_by UUID,
    record_version INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT pk_risk_assessment
        PRIMARY KEY (risk_assessment_id),

    CONSTRAINT fk_risk_assessment_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT ck_risk_assessment_score
        CHECK (overall_risk_score >= 0),

    CONSTRAINT ck_risk_assessment_confidence
        CHECK (
            confidence_score IS NULL
            OR (confidence_score >= 0 AND confidence_score <= 100)
        )
);

CREATE INDEX idx_risk_transaction
    ON transaction.risk_assessment (transaction_id);

CREATE INDEX idx_risk_timestamp
    ON transaction.risk_assessment (assessment_timestamp);

CREATE INDEX idx_risk_level
    ON transaction.risk_assessment (risk_level);

CREATE INDEX idx_risk_score
    ON transaction.risk_assessment (overall_risk_score);

CREATE INDEX idx_risk_result
    ON transaction.risk_assessment (assessment_result);

CREATE INDEX idx_risk_model
    ON transaction.risk_assessment (model_name, model_version);

CREATE INDEX idx_risk_stage
    ON transaction.risk_assessment (assessment_stage);