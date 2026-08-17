-- EFS-DB-002
-- V80 - Transaction Metadata
-- Controlled Physical Design Decision:
-- Completes physical typing for the approved transaction_metadata structure.

CREATE TABLE transaction.transaction_metadata (
    metadata_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    metadata_type VARCHAR(60) NOT NULL,
    metadata_json JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_metadata
        PRIMARY KEY (metadata_id),

    CONSTRAINT fk_transaction_metadata_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_metadata_transaction
    ON transaction.transaction_metadata (transaction_id);

CREATE INDEX idx_metadata_type
    ON transaction.transaction_metadata (metadata_type);

CREATE INDEX gin_metadata_json
    ON transaction.transaction_metadata USING GIN (metadata_json);