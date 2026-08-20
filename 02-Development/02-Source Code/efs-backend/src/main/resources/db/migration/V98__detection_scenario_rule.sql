-- EFS-DB-002
-- V98 - Detection Engine Scenario Rule

CREATE TABLE detection.scenario_rule (
    scenario_rule_id UUID NOT NULL DEFAULT uuidv7(),
    scenario_version_id UUID NOT NULL,
    rule_id UUID NOT NULL,
    rule_role VARCHAR(30),
    required BOOLEAN NOT NULL,
    evaluation_order SMALLINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_scenario_rule
        PRIMARY KEY (scenario_rule_id),

    CONSTRAINT fk_detection_scenario_rule_version
        FOREIGN KEY (scenario_version_id)
        REFERENCES detection.scenario_version (scenario_version_id),

    CONSTRAINT fk_detection_scenario_rule_rule
        FOREIGN KEY (rule_id)
        REFERENCES rules.rule (rule_id),

    CONSTRAINT uk_detection_scenario_rule
        UNIQUE (scenario_version_id, rule_id)
);

CREATE INDEX idx_detection_scenario_rule_version
    ON detection.scenario_rule (scenario_version_id);

CREATE INDEX idx_detection_scenario_rule_rule
    ON detection.scenario_rule (rule_id);

CREATE INDEX idx_detection_scenario_rule_order
    ON detection.scenario_rule (scenario_version_id, evaluation_order);