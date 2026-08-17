-- EFS-DB-002
-- V26 - Administration Tenant

CREATE TABLE administration.tenant (
    tenant_id UUID NOT NULL DEFAULT uuidv7(),
    organization_id UUID NOT NULL,
    tenant_code VARCHAR(50) NOT NULL,
    tenant_name VARCHAR(150) NOT NULL,
    status VARCHAR(20) NOT NULL,
    environment VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_tenant
        PRIMARY KEY (tenant_id),

    CONSTRAINT fk_tenant_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id)
);

CREATE INDEX idx_tenant_organization
    ON administration.tenant (organization_id);

CREATE INDEX idx_tenant_code
    ON administration.tenant (tenant_code);