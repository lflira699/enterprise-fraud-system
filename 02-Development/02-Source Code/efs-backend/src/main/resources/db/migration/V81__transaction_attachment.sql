-- EFS-DB-002
-- V81 - Transaction Attachment
-- Controlled Physical Design Decision:
-- Stores references and integrity metadata for files associated with transactions.
-- Physical file content remains outside the relational database.

CREATE TABLE transaction.transaction_attachment (
    attachment_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(60) NOT NULL,
    mime_type VARCHAR(120),
    file_size BIGINT,
    storage_uri VARCHAR(500) NOT NULL,
    checksum_sha256 CHAR(64),
    uploaded_by UUID,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_attachment
        PRIMARY KEY (attachment_id),

    CONSTRAINT fk_transaction_attachment_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_transaction_attachment_uploaded_by
        FOREIGN KEY (uploaded_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_attachment_transaction
    ON transaction.transaction_attachment (transaction_id);

CREATE INDEX idx_attachment_type
    ON transaction.transaction_attachment (file_type);