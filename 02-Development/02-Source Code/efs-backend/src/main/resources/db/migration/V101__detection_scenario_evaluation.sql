-- EFS-DB-002
-- V101 - Detection Engine Scenario Evaluation

CREATE TABLE detection.scenario_evaluation (
    evaluation_id UUID NOT NULL DEFAULT uuidv7(),
    scenario_id UUID NOT NULL,
    scenario_version_id UUID NOT NULL,
    transaction_id UUID,
    customer_id UUID,
    evaluation_status VARCHAR(30) NOT NULL,
    matched BOOLEAN NOT NULL,
    rule_count SMALLINT,
    matched_rule_count SMALLINT,
    required_evidence_count SMALLINT,
    available_evidence_count SMALLINT,
    confidence NUMERIC(8,4),
    risk_contribution NUMERIC(12,4),
    evaluated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    evaluation_duration_ms BIGINT,
    evaluation_context JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_scenario_evaluation
        PRIMARY KEY (evaluation_id),

    CONSTRAINT fk_detection_scenario_evaluation_scenario
        FOREIGN KEY (scenario_id)
        REFERENCES detection.scenario (scenario_id),

    CONSTRAINT fk_detection_scenario_evaluation_version
        FOREIGN KEY (scenario_version_id)
        REFERENCES detection.scenario_version (scenario_version_id),

    CONSTRAINT fk_detection_scenario_evaluation_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_detection_scenario_evaluation_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id)
);

CREATE INDEX idx_detection_scenario_evaluation_scenario
    ON detection.scenario_evaluation (scenario_id);

CREATE INDEX idx_detection_scenario_evaluation_version
    ON detection.scenario_evaluation (scenario_version_id);

CREATE INDEX idx_detection_scenario_evaluation_transaction
    ON detection.scenario_evaluation (transaction_id);

CREATE INDEX idx_detection_scenario_evaluation_customer
    ON detection.scenario_evaluation (customer_id);

CREATE INDEX idx_detection_scenario_evaluation_status
    ON detection.scenario_evaluation (evaluation_status);

CREATE INDEX idx_detection_scenario_evaluation_matched
    ON detection.scenario_evaluation (matched);

CREATE INDEX idx_detection_scenario_evaluation_date
    ON detection.scenario_evaluation (evaluated_at);

CREATE INDEX gin_detection_scenario_evaluation_context
    ON detection.scenario_evaluation USING GIN (evaluation_context);