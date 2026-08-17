-- EFS-DB-002
-- V9 - Customer Bank Account

CREATE TABLE customer.customer_bank_account (
    customer_bank_account_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    bank_name VARCHAR(150) NOT NULL,
    account_number VARCHAR(100) NOT NULL,
    account_type VARCHAR(30),
    currency_code VARCHAR(3),
    country_code VARCHAR(3),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_status VARCHAR(30),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP,
    deleted_by UUID,
    record_version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT pk_customer_bank_account
        PRIMARY KEY (customer_bank_account_id),

    CONSTRAINT fk_customer_bank_account_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id)
);

CREATE INDEX idx_customer_bank_account_customer
    ON customer.customer_bank_account (customer_id);

CREATE INDEX idx_customer_bank_account_number
    ON customer.customer_bank_account (account_number);

CREATE INDEX idx_customer_bank_account_primary
    ON customer.customer_bank_account (customer_id, is_primary);