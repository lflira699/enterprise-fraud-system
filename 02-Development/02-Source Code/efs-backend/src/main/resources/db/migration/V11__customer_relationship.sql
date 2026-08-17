-- EFS-DB-001 / EFS-DB-002
-- V11 - Customer Relationship

CREATE TABLE customer.customer_relationship (
    customer_relationship_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    related_customer_id UUID,
    relationship_type VARCHAR(50) NOT NULL,
    relationship_status VARCHAR(30) NOT NULL,
    relationship_description VARCHAR(500),
    effective_date DATE,
    expiration_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP,
    deleted_by UUID,
    record_version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT pk_customer_relationship
        PRIMARY KEY (customer_relationship_id),

    CONSTRAINT fk_customer_relationship_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT fk_customer_relationship_related
        FOREIGN KEY (related_customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT ck_customer_relationship_self
        CHECK (
            related_customer_id IS NULL
            OR customer_id <> related_customer_id
        ),

    CONSTRAINT ck_customer_relationship_dates
        CHECK (
            expiration_date IS NULL
            OR effective_date IS NULL
            OR expiration_date >= effective_date
        )
);

CREATE INDEX idx_customer_relationship_customer
    ON customer.customer_relationship (customer_id);

CREATE INDEX idx_customer_relationship_related
    ON customer.customer_relationship (related_customer_id);

CREATE INDEX idx_customer_relationship_type
    ON customer.customer_relationship (relationship_type);

CREATE INDEX idx_customer_relationship_status
    ON customer.customer_relationship (relationship_status);