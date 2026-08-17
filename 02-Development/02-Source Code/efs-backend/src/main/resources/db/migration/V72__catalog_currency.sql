-- EFS-DB-002
-- V72 - Catalog Currency
-- Controlled Physical Design Decision:
-- Specialized currency catalog based on ISO 4217.

CREATE TABLE catalog.currency (
    currency_id UUID NOT NULL DEFAULT uuidv7(),
    currency_code CHAR(3) NOT NULL,
    numeric_code CHAR(3),
    currency_name VARCHAR(150) NOT NULL,
    minor_unit SMALLINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_currency
        PRIMARY KEY (currency_id),

    CONSTRAINT uk_currency_code
        UNIQUE (currency_code),

    CONSTRAINT uk_currency_numeric_code
        UNIQUE (numeric_code)
);

CREATE INDEX idx_currency_name
    ON catalog.currency (currency_name);

CREATE INDEX idx_currency_status
    ON catalog.currency (status);