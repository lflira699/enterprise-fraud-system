-- EFS-DB-002
-- V25 - Administration Organization

CREATE TABLE administration.organization (
    organization_id UUID NOT NULL DEFAULT uuidv7(),
    organization_code VARCHAR(50) NOT NULL,
    legal_name VARCHAR(250) NOT NULL,
    commercial_name VARCHAR(200),
    tax_identifier VARCHAR(50),
    country_code CHAR(2) NOT NULL,
    timezone VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_organization
        PRIMARY KEY (organization_id)
);

CREATE INDEX idx_organization_code
    ON administration.organization (organization_code);

CREATE INDEX idx_organization_country
    ON administration.organization (country_code);

CREATE INDEX idx_organization_status
    ON administration.organization (status);