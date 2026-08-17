-- EFS-DB-002
-- V71 - Catalog Country
-- Controlled Physical Design Decision:
-- Specialized country catalog based on ISO 3166.

CREATE TABLE catalog.country (
    country_id UUID NOT NULL DEFAULT uuidv7(),
    country_code CHAR(2) NOT NULL,
    alpha3_code CHAR(3) NOT NULL,
    numeric_code CHAR(3),
    country_name VARCHAR(150) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_country
        PRIMARY KEY (country_id),

    CONSTRAINT uk_country_code
        UNIQUE (country_code),

    CONSTRAINT uk_country_alpha3
        UNIQUE (alpha3_code)
);

CREATE INDEX idx_country_name
    ON catalog.country (country_name);

CREATE INDEX idx_country_status
    ON catalog.country (status);