-- EFS-DB-002
-- V29 - Administration Role

CREATE TABLE administration.role (
    role_id UUID NOT NULL DEFAULT uuidv7(),
    organization_id UUID,
    role_code VARCHAR(60) NOT NULL,
    role_name VARCHAR(120) NOT NULL,
    description TEXT,
    is_system BOOLEAN NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_role
        PRIMARY KEY (role_id),

    CONSTRAINT fk_role_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id)
);

CREATE INDEX idx_role_code
    ON administration.role (role_code);

CREATE INDEX idx_role_status
    ON administration.role (status);

CREATE INDEX idx_role_organization
    ON administration.role (organization_id);