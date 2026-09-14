-- EFS Organizational Scope Persistence Alignment Contract v1
-- ScenarioActivation Aggregate organizational ownership

ALTER TABLE detection.scenario_activation
    ADD COLUMN organization_id UUID,
    ADD COLUMN tenant_id UUID;

-- Primary ownership inheritance:
-- ScenarioActivation -> Transaction -> Organization / Tenant
UPDATE detection.scenario_activation AS sa
SET
    organization_id = tx.organization_id,
    tenant_id = tx.tenant_id
FROM transaction.transaction AS tx
WHERE sa.transaction_id = tx.transaction_id;

-- Fallback ownership inheritance when no Transaction exists:
-- ScenarioActivation -> Customer -> Tenant -> Organization
UPDATE detection.scenario_activation AS sa
SET
    organization_id = t.organization_id,
    tenant_id = c.tenant_id
FROM customer.customer AS c
JOIN administration.tenant AS t
    ON t.tenant_id = c.tenant_id
WHERE sa.transaction_id IS NULL
  AND sa.customer_id = c.customer_id;

-- When Transaction and Customer are both present they must resolve
-- to exactly the same organizational scope.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM detection.scenario_activation AS sa
        JOIN transaction.transaction AS tx
            ON tx.transaction_id = sa.transaction_id
        JOIN customer.customer AS c
            ON c.customer_id = sa.customer_id
        LEFT JOIN administration.tenant AS t
            ON t.tenant_id = c.tenant_id
        WHERE sa.transaction_id IS NOT NULL
          AND sa.customer_id IS NOT NULL
          AND (
                c.tenant_id IS NULL
                OR t.organization_id IS NULL
                OR tx.organization_id
                    IS DISTINCT FROM t.organization_id
                OR tx.tenant_id
                    IS DISTINCT FROM c.tenant_id
              )
    ) THEN
        RAISE EXCEPTION
            'V158 detected ScenarioActivation Transaction/Customer organizational scope mismatch';
    END IF;
END
$$;

-- Fail closed when ownership cannot be reconstructed.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM detection.scenario_activation
        WHERE organization_id IS NULL
    ) THEN
        RAISE EXCEPTION
            'V158 cannot resolve organization_id for one or more scenario activation records';
    END IF;
END
$$;

-- Validate tenant / organization consistency before constraints.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM detection.scenario_activation AS sa
        WHERE sa.tenant_id IS NOT NULL
          AND NOT EXISTS (
              SELECT 1
              FROM administration.tenant AS t
              WHERE t.tenant_id = sa.tenant_id
                AND t.organization_id = sa.organization_id
          )
    ) THEN
        RAISE EXCEPTION
            'V158 detected scenario activation tenant_id inconsistent with organization_id';
    END IF;
END
$$;

ALTER TABLE detection.scenario_activation
    ALTER COLUMN organization_id SET NOT NULL;

ALTER TABLE detection.scenario_activation
    ADD CONSTRAINT fk_scenario_activation_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id);

ALTER TABLE detection.scenario_activation
    ADD CONSTRAINT fk_scenario_activation_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id);

CREATE INDEX idx_scenario_activation_organization_tenant
    ON detection.scenario_activation (
        organization_id,
        tenant_id
    );
