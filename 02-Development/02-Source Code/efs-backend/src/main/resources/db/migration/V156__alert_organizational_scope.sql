-- EFS Organizational Scope Persistence Alignment Contract v1
-- Alert Aggregate organizational ownership

ALTER TABLE alert.alert
    ADD COLUMN organization_id UUID,
    ADD COLUMN tenant_id UUID;

-- Primary deterministic backfill:
-- Alert -> Transaction -> Organization / Tenant
UPDATE alert.alert AS a
SET
    organization_id = t.organization_id,
    tenant_id = t.tenant_id
FROM transaction.transaction AS t
WHERE a.transaction_id = t.transaction_id;

-- Historical fallback:
-- Alert -> Transaction Decision -> Transaction -> Organization / Tenant
UPDATE alert.alert AS a
SET
    organization_id = t.organization_id,
    tenant_id = t.tenant_id
FROM transaction.transaction_decision AS d
JOIN transaction.transaction AS t
    ON t.transaction_id = d.transaction_id
WHERE a.decision_id = d.decision_id
  AND a.organization_id IS NULL;

-- Validate existing Alert -> Decision -> Transaction consistency.
-- When both references exist, they must identify the same Transaction.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM alert.alert AS a
        JOIN transaction.transaction_decision AS d
            ON d.decision_id = a.decision_id
        WHERE a.transaction_id IS NOT NULL
          AND a.transaction_id <> d.transaction_id
    ) THEN
        RAISE EXCEPTION
            'V156 detected alert transaction_id inconsistent with decision_id';
    END IF;
END
$$;

-- Fail closed if organizational ownership cannot be reconstructed.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM alert.alert
        WHERE organization_id IS NULL
    ) THEN
        RAISE EXCEPTION
            'V156 cannot resolve organization_id for one or more alert records';
    END IF;
END
$$;

-- Validate tenant / organization consistency before constraints are applied.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM alert.alert AS a
        WHERE a.tenant_id IS NOT NULL
          AND NOT EXISTS (
              SELECT 1
              FROM administration.tenant AS t
              WHERE t.tenant_id = a.tenant_id
                AND t.organization_id = a.organization_id
          )
    ) THEN
        RAISE EXCEPTION
            'V156 detected alert tenant_id inconsistent with organization_id';
    END IF;
END
$$;

ALTER TABLE alert.alert
    ALTER COLUMN organization_id SET NOT NULL;

ALTER TABLE alert.alert
    ADD CONSTRAINT fk_alert_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id);

ALTER TABLE alert.alert
    ADD CONSTRAINT fk_alert_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id);

CREATE INDEX idx_alert_organization_tenant
    ON alert.alert (organization_id, tenant_id);
