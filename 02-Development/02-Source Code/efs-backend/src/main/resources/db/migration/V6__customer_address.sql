-- EFS-DB-002
-- V6 - Customer Address

CREATE TABLE customer.customer_address (
    customer_address_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    address_type VARCHAR(30) NOT NULL,
    address_line_1 VARCHAR(250) NOT NULL,
    address_line_2 VARCHAR(250),
    city VARCHAR(120),
    state VARCHAR(120),
    postal_code VARCHAR(30),
    country_code VARCHAR(3) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    effective_date DATE,
    expiration_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP,
    deleted_by UUID,
    record_version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT pk_customer_address
        PRIMARY KEY (customer_address_id),

    CONSTRAINT fk_customer_address_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id)
);

CREATE INDEX idx_customer_address_customer
    ON customer.customer_address (customer_id);

CREATE INDEX idx_customer_address_country
    ON customer.customer_address (country_code);

CREATE INDEX idx_customer_address_primary
    ON customer.customer_address (customer_id, is_primary);