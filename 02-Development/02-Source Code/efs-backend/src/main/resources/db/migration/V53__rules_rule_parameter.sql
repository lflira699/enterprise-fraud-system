-- EFS-DB-002
-- V53 - Rule Engine Rule Parameter

CREATE TABLE rules.rule_parameter (
    parameter_id UUID NOT NULL DEFAULT uuidv7(),
    rule_version_id UUID NOT NULL,
    parameter_name VARCHAR(100) NOT NULL,
    parameter_type VARCHAR(30) NOT NULL,
    parameter_value JSONB NOT NULL,
    is_sensitive BOOLEAN NOT NULL,
    validation_expression TEXT,

    CONSTRAINT pk_rule_parameter
        PRIMARY KEY (parameter_id),

    CONSTRAINT fk_rule_parameter_rule_version
        FOREIGN KEY (rule_version_id)
        REFERENCES rules.rule_version (rule_version_id)
);

CREATE INDEX idx_parameter_rule
    ON rules.rule_parameter (rule_version_id);

CREATE INDEX idx_parameter_name
    ON rules.rule_parameter (parameter_name);