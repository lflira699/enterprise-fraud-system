
-- EFS-DB-002
-- V54 - Rule Engine Rule Group

CREATE TABLE rules.rule_group (
    rule_group_id UUID NOT NULL DEFAULT uuidv7(),
    group_code VARCHAR(60) NOT NULL,
    group_name VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    status VARCHAR(20) NOT NULL,
    execution_order SMALLINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_rule_group
        PRIMARY KEY (rule_group_id),

    CONSTRAINT uk_rule_group_code
        UNIQUE (group_code)
);

CREATE INDEX idx_rule_group_code
    ON rules.rule_group (group_code);

CREATE INDEX idx_rule_group_status
    ON rules.rule_group (status);

CREATE INDEX idx_rule_group_category
    ON rules.rule_group (category);