-- EFS-DB-002
-- V82 - Transaction History
-- Controlled Physical Design Decision:
-- Stores complete immutable historical snapshots of transaction state.

CREATE TABLE transaction.transaction_history (
    history_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    version_number INTEGER NOT NULL,
    snapshot_json JSONB NOT NULL,
    change_reason TEXT,
    changed_by UUID,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_history
        PRIMARY KEY (history_id),

    CONSTRAINT fk_transaction_history_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_transaction_history_changed_by
        FOREIGN KEY (changed_by)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT uq_transaction_history_version
        UNIQUE (transaction_id, version_number)
);

CREATE INDEX idx_transaction_history_transaction
    ON transaction.transaction_history (transaction_id);

CREATE INDEX idx_transaction_history_date
    ON transaction.transaction_history (changed_at);

CREATE INDEX idx_transaction_history_user
    ON transaction.transaction_history (changed_by);

CREATE INDEX gin_transaction_history_snapshot
    ON transaction.transaction_history USING GIN (snapshot_json);