-- EFS-DB-002
-- V76 - Catalog Transaction Type
-- Controlled Physical Design Decision:
-- Specialized catalog of transaction types used by EFS.

CREATE TABLE catalog.transaction_type (
    transaction_type_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_type_code VARCHAR(60) NOT NULL,
    transaction_type_name VARCHAR(150) NOT NULL,
    description TEXT,
    display_order SMALLINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_type
        PRIMARY KEY (transaction_type_id),

    CONSTRAINT uk_transaction_type_code
        UNIQUE (transaction_type_code)
);

CREATE INDEX idx_transaction_type_name
    ON catalog.transaction_type (transaction_type_name);

CREATE INDEX idx_transaction_type_order
    ON catalog.transaction_type (display_order);

CREATE INDEX idx_transaction_type_status
    ON catalog.transaction_type (status);