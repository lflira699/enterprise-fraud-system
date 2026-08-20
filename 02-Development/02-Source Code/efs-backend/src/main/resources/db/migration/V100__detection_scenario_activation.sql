-- EFS-DB-002
-- V100 - Detection Engine Scenario Activation

CREATE TABLE detection.scenario_activation (
    activation_id UUID NOT NULL DEFAULT uuidv7(),
    scenario_id UUID NOT NULL,
    scenario_version_id UUID NOT NULL,
    transaction_id UUID,
    customer_id UUID,
    activation_status VARCHAR(30) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    confidence NUMERIC(8,4),
    risk_score NUMERIC(12,4),
    triggered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    activation_reason TEXT,
    decision_context JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_scenario_activation
        PRIMARY KEY (activation_id),

    CONSTRAINT fk_detection_scenario_activation_scenario
        FOREIGN KEY (scenario_id)
        REFERENCES detection.scenario (scenario_id),

    CONSTRAINT fk_detection_scenario_activation_version
        FOREIGN KEY (scenario_version_id)
        REFERENCES detection.scenario_version (scenario_version_id),

    CONSTRAINT fk_detection_scenario_activation_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_detection_scenario_activation_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id)
);

CREATE INDEX idx_detection_scenario_activation_scenario
    ON detection.scenario_activation (scenario_id);

CREATE INDEX idx_detection_scenario_activation_version
    ON detection.scenario_activation (scenario_version_id);

CREATE INDEX idx_detection_scenario_activation_transaction
    ON detection.scenario_activation (transaction_id);

CREATE INDEX idx_detection_scenario_activation_customer
    ON detection.scenario_activation (customer_id);

CREATE INDEX idx_detection_scenario_activation_status
    ON detection.scenario_activation (activation_status);

CREATE INDEX idx_detection_scenario_activation_severity
    ON detection.scenario_activation (severity);

CREATE INDEX idx_detection_scenario_activation_triggered
    ON detection.scenario_activation (triggered_at);

CREATE INDEX gin_detection_scenario_activation_context
    ON detection.scenario_activation USING GIN (decision_context);