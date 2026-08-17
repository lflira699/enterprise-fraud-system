-- EFS-DB-002
-- V49 - Rule Engine Rule

CREATE TABLE rules.rule (
    rule_id UUID NOT NULL DEFAULT uuidv7(),
    rule_code VARCHAR(60) NOT NULL,
    rule_name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    priority SMALLINT NOT NULL,
    owner_team VARCHAR(100),
    current_version INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_rule
        PRIMARY KEY (rule_id),

    CONSTRAINT uk_rule_code
        UNIQUE (rule_code)
);

CREATE INDEX idx_rule_code
    ON rules.rule (rule_code);

CREATE INDEX idx_rule_status
    ON rules.rule (status);

CREATE INDEX idx_rule_category
    ON rules.rule (category);

CREATE INDEX idx_rule_priority
    ON rules.rule (priority);