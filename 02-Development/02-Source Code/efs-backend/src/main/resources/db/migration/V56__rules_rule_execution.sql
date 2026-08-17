-- EFS-DB-002
-- V56 - Rule Engine Rule Execution

CREATE TABLE rules.rule_execution (
    execution_id UUID NOT NULL DEFAULT uuidv7(),
    rule_id UUID,
    rule_version_id UUID,
    policy_id UUID,
    transaction_id UUID NOT NULL,
    execution_status VARCHAR(30) NOT NULL,
    matched BOOLEAN NOT NULL,
    execution_time_ms INTEGER NOT NULL,
    error_code VARCHAR(50),
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    engine_instance VARCHAR(100),

    CONSTRAINT pk_rule_execution
        PRIMARY KEY (execution_id),

    CONSTRAINT fk_rule_execution_rule
        FOREIGN KEY (rule_id)
        REFERENCES rules.rule (rule_id),

    CONSTRAINT fk_rule_execution_rule_version
        FOREIGN KEY (rule_version_id)
        REFERENCES rules.rule_version (rule_version_id),

    CONSTRAINT fk_rule_execution_policy
        FOREIGN KEY (policy_id)
        REFERENCES rules.rule_policy (policy_id),

    CONSTRAINT fk_rule_execution_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_execution_rule
    ON rules.rule_execution (rule_id);

CREATE INDEX idx_execution_policy
    ON rules.rule_execution (policy_id);

CREATE INDEX idx_execution_transaction
    ON rules.rule_execution (transaction_id);

CREATE INDEX idx_execution_status
    ON rules.rule_execution (execution_status);

CREATE INDEX idx_execution_date
    ON rules.rule_execution (executed_at);