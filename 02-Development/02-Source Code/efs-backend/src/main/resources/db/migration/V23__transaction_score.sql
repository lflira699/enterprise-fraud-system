-- EFS-DB-002
-- V23 - Transaction Score

CREATE TABLE transaction.transaction_score (
    score_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    score_type VARCHAR(40) NOT NULL,
    score_value NUMERIC(8,2) NOT NULL,
    score_weight NUMERIC(5,2),
    scoring_model VARCHAR(80),
    model_version VARCHAR(20),
    calculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_score
        PRIMARY KEY (score_id),

    CONSTRAINT fk_transaction_score_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_score_transaction
    ON transaction.transaction_score (transaction_id);

CREATE INDEX idx_score_type
    ON transaction.transaction_score (score_type);

CREATE INDEX idx_score_model
    ON transaction.transaction_score (scoring_model);

CREATE INDEX idx_score_value
    ON transaction.transaction_score (score_value);