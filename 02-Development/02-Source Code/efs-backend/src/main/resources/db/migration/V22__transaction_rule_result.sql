-- EFS-DB-002
-- V22 - Transaction Rule Result

CREATE TABLE transaction.transaction_rule_result (
    rule_result_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    rule_id UUID NOT NULL,
    rule_version INTEGER NOT NULL,
    execution_order SMALLINT NOT NULL,
    execution_time_ms INTEGER,
    evaluation_result VARCHAR(20) NOT NULL,
    risk_points NUMERIC(8,2) NOT NULL,
    recommended_action VARCHAR(30),
    explanation TEXT,
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_rule_result
        PRIMARY KEY (rule_result_id),

    CONSTRAINT fk_transaction_rule_result_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_rule_result_transaction
    ON transaction.transaction_rule_result (transaction_id);

CREATE INDEX idx_rule_result_rule
    ON transaction.transaction_rule_result (rule_id);

CREATE INDEX idx_rule_result_match
    ON transaction.transaction_rule_result (evaluation_result);

CREATE INDEX idx_rule_result_executed
    ON transaction.transaction_rule_result (executed_at);