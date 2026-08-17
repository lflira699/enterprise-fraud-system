-- EFS-DB-002
-- V51 - Rule Engine Rule Condition
-- Controlled Physical Design Decision:
-- Completes physical typing for the approved rule_condition structure.

CREATE TABLE rules.rule_condition (
    condition_id UUID NOT NULL DEFAULT uuidv7(),
    rule_version_id UUID NOT NULL,
    condition_order SMALLINT NOT NULL,
    attribute_name VARCHAR(150) NOT NULL,
    comparison_operator VARCHAR(30) NOT NULL,
    comparison_value JSONB NOT NULL,
    logical_operator VARCHAR(20),
    is_required BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_rule_condition
        PRIMARY KEY (condition_id),

    CONSTRAINT fk_rule_condition_rule_version
        FOREIGN KEY (rule_version_id)
        REFERENCES rules.rule_version (rule_version_id)
);

CREATE INDEX idx_condition_rule
    ON rules.rule_condition (rule_version_id);

CREATE INDEX idx_condition_attribute
    ON rules.rule_condition (attribute_name);