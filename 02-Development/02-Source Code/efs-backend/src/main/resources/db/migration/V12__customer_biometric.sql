-- EFS-DB-002
-- V12 - Customer Biometric

CREATE TABLE customer.customer_biometric (
    biometric_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    biometric_type VARCHAR(30) NOT NULL,
    verification_status VARCHAR(30) NOT NULL,
    verification_score NUMERIC(8,2),
    provider_reference VARCHAR(255),
    enrolled_at TIMESTAMP,
    last_verified_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP,
    record_version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT pk_customer_biometric
        PRIMARY KEY (biometric_id),

    CONSTRAINT fk_customer_biometric_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id)
);

CREATE INDEX idx_customer_biometric_customer
    ON customer.customer_biometric (customer_id);

CREATE INDEX idx_customer_biometric_type
    ON customer.customer_biometric (biometric_type);

CREATE INDEX idx_customer_biometric_status
    ON customer.customer_biometric (verification_status);

CREATE INDEX idx_customer_biometric_verified
    ON customer.customer_biometric (last_verified_at);