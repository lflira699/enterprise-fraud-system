-- EFS-DB-002
-- V79 - Transaction Status History

CREATE TABLE transaction.transaction_status_history (
    history_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    previous_status VARCHAR(30),
    current_status VARCHAR(30) NOT NULL,
    change_reason TEXT,
    changed_by UUID,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_status_history
        PRIMARY KEY (history_id),

    CONSTRAINT fk_transaction_status_history_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_transaction_status_history_changed_by
        FOREIGN KEY (changed_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_status_transaction
    ON transaction.transaction_status_history (transaction_id);

CREATE INDEX idx_status_current
    ON transaction.transaction_status_history (current_status);

CREATE INDEX idx_status_date
    ON transaction.transaction_status_history (changed_at);