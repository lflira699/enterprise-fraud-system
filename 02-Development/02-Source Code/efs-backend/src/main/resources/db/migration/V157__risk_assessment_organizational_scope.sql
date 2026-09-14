-- EFS Organizational Scope Persistence Alignment Contract v1
-- RiskAssessment Aggregate organizational ownership

ALTER TABLE transaction.risk_assessment
    ADD COLUMN organization_id UUID,
    ADD COLUMN tenant_id UUID;

-- Deterministic ownership inheritance:
-- RiskAssessment -> Transaction -> Organization / Tenant
UPDATE transaction.risk_assessment AS r
SET
    organization_id = t.organization_id,
    tenant_id = t.tenant_id
FROM transaction.transaction AS t
WHERE r.transaction_id = t.transaction_id;

-- Fail closed if organizational ownership cannot be reconstructed.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM transaction.risk_assessment
        WHERE organization_id IS NULL
    ) THEN
        RAISE EXCEPTION
            'V157 cannot resolve organization_id for one or more risk assessment records';
    END IF;
END
$$;

-- Validate tenant / organization consistency before constraints are applied.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM transaction.risk_assessment AS r
        WHERE r.tenant_id IS NOT NULL
          AND NOT EXISTS (
              SELECT 1
              FROM administration.tenant AS t
              WHERE t.tenant_id = r.tenant_id
                AND t.organization_id = r.organization_id
          )
    ) THEN
        RAISE EXCEPTION
            'V157 detected risk assessment tenant_id inconsistent with organization_id';
    END IF;
END
$$;

ALTER TABLE transaction.risk_assessment
    ALTER COLUMN organization_id SET NOT NULL;

ALTER TABLE transaction.risk_assessment
    ADD CONSTRAINT fk_risk_assessment_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id);

ALTER TABLE transaction.risk_assessment
    ADD CONSTRAINT fk_risk_assessment_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id);

CREATE INDEX idx_risk_assessment_organization_tenant
    ON transaction.risk_assessment (organization_id, tenant_id);
