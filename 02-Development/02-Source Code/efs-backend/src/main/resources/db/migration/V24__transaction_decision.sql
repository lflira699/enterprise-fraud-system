-- EFS-DB-002
-- V24 - Transaction Decision

CREATE TABLE transaction.transaction_decision (
    decision_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    decision_type VARCHAR(40) NOT NULL,
    decision_source VARCHAR(40) NOT NULL,
    confidence_score NUMERIC(5,2),
    decision_reason TEXT,
    approved_by UUID,
    decision_timestamp TIMESTAMP NOT NULL,
    is_final BOOLEAN NOT NULL,

    CONSTRAINT pk_transaction_decision
        PRIMARY KEY (decision_id),

    CONSTRAINT fk_transaction_decision_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_decision_transaction
    ON transaction.transaction_decision (transaction_id);

CREATE INDEX idx_decision_source
    ON transaction.transaction_decision (decision_source);

CREATE INDEX idx_decision_type
    ON transaction.transaction_decision (decision_type);

CREATE INDEX idx_decision_final
    ON transaction.transaction_decision (is_final);