-- EFS-DB-002
-- V117 - System Configuration Scope Integrity
--
-- Controlled implementation decision:
-- Configuration resolution hierarchy:
--   1. Organization + Tenant
--   2. Organization
--   3. Global
--
-- A tenant-scoped configuration must always belong to an organization.
-- Only one configuration key may exist for the same effective scope.

-- Prevent tenant configuration without organization context.

ALTER TABLE administration.system_configuration
    ADD CONSTRAINT ck_system_configuration_tenant_requires_organization
    CHECK (
        tenant_id IS NULL
        OR organization_id IS NOT NULL
    );

-- Global configuration:
-- organization_id IS NULL
-- tenant_id IS NULL

CREATE UNIQUE INDEX uk_system_configuration_global_key
    ON administration.system_configuration (configuration_key)
    WHERE organization_id IS NULL
      AND tenant_id IS NULL;

-- Organization configuration:
-- organization_id IS NOT NULL
-- tenant_id IS NULL

CREATE UNIQUE INDEX uk_system_configuration_organization_key
    ON administration.system_configuration (
        organization_id,
        configuration_key
    )
    WHERE organization_id IS NOT NULL
      AND tenant_id IS NULL;

-- Tenant configuration:
-- organization_id IS NOT NULL
-- tenant_id IS NOT NULL

CREATE UNIQUE INDEX uk_system_configuration_tenant_key
    ON administration.system_configuration (
        organization_id,
        tenant_id,
        configuration_key
    )
    WHERE organization_id IS NOT NULL
      AND tenant_id IS NOT NULL;