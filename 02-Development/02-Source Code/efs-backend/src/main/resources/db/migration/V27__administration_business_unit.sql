-- EFS-DB-002
-- V27 - Administration Business Unit

CREATE TABLE administration.business_unit (
    business_unit_id UUID NOT NULL DEFAULT uuidv7(),
    organization_id UUID NOT NULL,
    unit_code VARCHAR(50) NOT NULL,
    unit_name VARCHAR(150) NOT NULL,
    manager_user_id UUID,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_business_unit
        PRIMARY KEY (business_unit_id),

    CONSTRAINT fk_business_unit_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id)
);

CREATE INDEX idx_business_unit_organization
    ON administration.business_unit (organization_id);

CREATE INDEX idx_business_unit_code
    ON administration.business_unit (unit_code);