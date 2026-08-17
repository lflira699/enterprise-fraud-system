-- EFS-DB-002
-- V4 - Customer Document

CREATE TABLE customer.customer_document (
    document_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_number VARCHAR(100) NOT NULL,
    issuing_country VARCHAR(3),
    issue_date DATE,
    expiration_date DATE,
    verification_status VARCHAR(30),
    verified_at TIMESTAMP,
    verified_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_customer_document
        PRIMARY KEY (document_id),

    CONSTRAINT fk_customer_document_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT uk_customer_document
        UNIQUE (customer_id, document_type, document_number)
);

CREATE INDEX idx_document_number
    ON customer.customer_document (document_number);

CREATE INDEX idx_customer_document
    ON customer.customer_document (customer_id);

CREATE INDEX idx_document_status
    ON customer.customer_document (verification_status);