-- EFS-DB-002
-- V52 - Rule Engine Rule Action
-- Controlled Physical Design Decision:
-- Completes physical typing for the approved rule_action structure.

CREATE TABLE rules.rule_action (
    action_id UUID NOT NULL DEFAULT uuidv7(),
    rule_version_id UUID NOT NULL,
    action_type VARCHAR(40) NOT NULL,
    execution_order SMALLINT NOT NULL,
    parameter_json JSONB,
    is_async BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_rule_action
        PRIMARY KEY (action_id),

    CONSTRAINT fk_rule_action_rule_version
        FOREIGN KEY (rule_version_id)
        REFERENCES rules.rule_version (rule_version_id)
);

CREATE INDEX idx_action_rule
    ON rules.rule_action (rule_version_id);

CREATE INDEX idx_action_type
    ON rules.rule_action (action_type);