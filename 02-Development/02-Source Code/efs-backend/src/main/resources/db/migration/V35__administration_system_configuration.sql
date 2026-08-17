-- EFS-DB-002
-- V35 - Administration System Configuration

CREATE TABLE administration.system_configuration (
    configuration_id UUID NOT NULL DEFAULT uuidv7(),
    configuration_key VARCHAR(150) NOT NULL,
    configuration_value TEXT NOT NULL,
    configuration_type VARCHAR(50) NOT NULL,
    tenant_id UUID,
    organization_id UUID,
    encrypted BOOLEAN NOT NULL DEFAULT FALSE,
    updated_by UUID,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_system_configuration
        PRIMARY KEY (configuration_id),

    CONSTRAINT fk_system_configuration_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id),

    CONSTRAINT fk_system_configuration_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_system_configuration_updated_by
        FOREIGN KEY (updated_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_configuration_key
    ON administration.system_configuration (configuration_key);

CREATE INDEX idx_configuration_tenant
    ON administration.system_configuration (tenant_id);

CREATE INDEX idx_configuration_organization
    ON administration.system_configuration (organization_id);