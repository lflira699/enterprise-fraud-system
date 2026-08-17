-- EFS-DB-002
-- V7 - Customer Phone

CREATE TABLE customer.customer_phone (
    customer_phone_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    phone_type VARCHAR(30) NOT NULL,
    country_code VARCHAR(10),
    phone_number VARCHAR(30) NOT NULL,
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

    CONSTRAINT pk_customer_phone
        PRIMARY KEY (customer_phone_id),

    CONSTRAINT fk_customer_phone_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id)
);

CREATE INDEX idx_customer_phone_customer
    ON customer.customer_phone (customer_id);

CREATE INDEX idx_customer_phone_number
    ON customer.customer_phone (phone_number);

CREATE INDEX idx_customer_phone_primary
    ON customer.customer_phone (customer_id, is_primary);