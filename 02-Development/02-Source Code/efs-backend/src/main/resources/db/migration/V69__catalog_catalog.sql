-- EFS-DB-002
-- V69 - Catalog

CREATE TABLE catalog.catalog (
    catalog_id UUID NOT NULL DEFAULT uuidv7(),
    catalog_code VARCHAR(60) NOT NULL,
    catalog_name VARCHAR(150) NOT NULL,
    description TEXT,
    organization_id UUID,
    tenant_id UUID,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_catalog
        PRIMARY KEY (catalog_id),

    CONSTRAINT fk_catalog_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_catalog_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id)
);

CREATE INDEX idx_catalog_code
    ON catalog.catalog (catalog_code);

CREATE INDEX idx_catalog_status
    ON catalog.catalog (status);

CREATE INDEX idx_catalog_organization
    ON catalog.catalog (organization_id);