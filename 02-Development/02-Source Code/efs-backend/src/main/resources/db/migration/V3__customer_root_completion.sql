-- EFS-DB-002
-- V3 - Customer Root Completion

ALTER TABLE customer.customer
    ALTER COLUMN customer_id SET DEFAULT uuidv7();

CREATE INDEX IDX_CUSTOMER_NUMBER
    ON customer.customer (customer_number);

CREATE INDEX IDX_CUSTOMER_STATUS
    ON customer.customer (customer_status);

CREATE INDEX IDX_CUSTOMER_RISK
    ON customer.customer (risk_level);

CREATE INDEX IDX_CUSTOMER_COUNTRY
    ON customer.customer (country_id);

CREATE INDEX IDX_CUSTOMER_CREATED_AT
    ON customer.customer (created_at);