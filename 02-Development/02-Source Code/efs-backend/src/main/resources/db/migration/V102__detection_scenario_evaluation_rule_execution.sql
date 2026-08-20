-- EFS-DB-002
-- V102 - Detection Scenario Evaluation Rule Execution

CREATE TABLE detection.scenario_evaluation_rule_execution (
    evaluation_rule_execution_id UUID NOT NULL DEFAULT uuidv7(),
    evaluation_id UUID NOT NULL,
    execution_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_evaluation_rule_execution
        PRIMARY KEY (evaluation_rule_execution_id),

    CONSTRAINT fk_detection_evaluation_rule_execution_evaluation
        FOREIGN KEY (evaluation_id)
        REFERENCES detection.scenario_evaluation (evaluation_id),

    CONSTRAINT fk_detection_evaluation_rule_execution_execution
        FOREIGN KEY (execution_id)
        REFERENCES rules.rule_execution (execution_id),

    CONSTRAINT uk_detection_evaluation_rule_execution
        UNIQUE (evaluation_id, execution_id)
);

CREATE INDEX idx_detection_evaluation_rule_execution_evaluation
    ON detection.scenario_evaluation_rule_execution (evaluation_id);

CREATE INDEX idx_detection_evaluation_rule_execution_execution
    ON detection.scenario_evaluation_rule_execution (execution_id);