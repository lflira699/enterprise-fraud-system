-- EFS-DB-002
-- V2 - Physical Tables
-- Customer Aggregate

CREATE TABLE customer.customer (
    customer_id UUID NOT NULL,
    customer_number VARCHAR(50) NOT NULL,
    customer_type VARCHAR(30) NOT NULL,
    first_name VARCHAR(120),
    middle_name VARCHAR(120),
    last_name VARCHAR(120),
    second_last_name VARCHAR(120),
    legal_name VARCHAR(250),
    date_of_birth DATE,
    country_id UUID,
    risk_level VARCHAR(20) NOT NULL DEFAULT 'LOW',
    risk_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    customer_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    record_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    record_version INTEGER NOT NULL DEFAULT 1,
    tenant_id UUID,
    deleted_at TIMESTAMP,

    CONSTRAINT PK_CUSTOMER
        PRIMARY KEY (customer_id)
);