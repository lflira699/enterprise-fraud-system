-- EFS-DB-002
-- V8 - Customer Email

CREATE TABLE customer.customer_email (
    customer_email_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    email_type VARCHAR(30) NOT NULL,
    email_address VARCHAR(254) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP,
    deleted_by UUID,
    record_version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT pk_customer_email
        PRIMARY KEY (customer_email_id),

    CONSTRAINT fk_customer_email_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id)
);

CREATE INDEX idx_customer_email_customer
    ON customer.customer_email (customer_id);

CREATE INDEX idx_customer_email_address
    ON customer.customer_email (email_address);

CREATE INDEX idx_customer_email_primary
    ON customer.customer_email (customer_id, is_primary);