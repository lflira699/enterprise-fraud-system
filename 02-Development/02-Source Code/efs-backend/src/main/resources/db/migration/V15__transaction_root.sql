-- EFS-DB-001 / EFS-DB-002
-- V15 - Transaction Root

CREATE TABLE transaction.transaction (
    transaction_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_reference VARCHAR(100) NOT NULL,
    external_reference VARCHAR(150),
    customer_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    transaction_type VARCHAR(40) NOT NULL,
    transaction_subtype VARCHAR(40),
    amount NUMERIC(20,2) NOT NULL DEFAULT 0,
    currency_code CHAR(3) NOT NULL,
    exchange_rate NUMERIC(18,8),
    transaction_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processing_datetime TIMESTAMP,
    transaction_status VARCHAR(30) NOT NULL DEFAULT 'RECEIVED',
    final_decision VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    fraud_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    correlation_id UUID,
    request_id UUID,
    session_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_by UUID,
    record_version INTEGER NOT NULL DEFAULT 1,
    tenant_id UUID,
    deleted_at TIMESTAMP DEFAULT NULL,

    CONSTRAINT pk_transaction
        PRIMARY KEY (transaction_id),

    CONSTRAINT uk_transaction_reference
        UNIQUE (transaction_reference),

    CONSTRAINT fk_transaction_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT ck_transaction_amount
        CHECK (amount > 0)
);

CREATE INDEX idx_transaction_reference
    ON transaction.transaction (transaction_reference);

CREATE INDEX idx_transaction_customer
    ON transaction.transaction (customer_id);

CREATE INDEX idx_transaction_status
    ON transaction.transaction (transaction_status);

CREATE INDEX idx_transaction_date
    ON transaction.transaction (transaction_datetime);

CREATE INDEX idx_transaction_organization
    ON transaction.transaction (organization_id);

CREATE INDEX idx_transaction_decision
    ON transaction.transaction (final_decision);

CREATE INDEX idx_transaction_correlation
    ON transaction.transaction (correlation_id);

CREATE INDEX idx_transaction_request
    ON transaction.transaction (request_id);

CREATE INDEX idx_transaction_session
    ON transaction.transaction (session_id);