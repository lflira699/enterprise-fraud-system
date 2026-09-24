-- EFS Scenario Evaluation Organizational Scope Contract v1.1
-- Scenario Evaluation Security Contract v1.1

ALTER TABLE detection.scenario_evaluation
    ADD COLUMN organization_id UUID,
    ADD COLUMN tenant_id UUID;

-- Validate every referenced Transaction before deriving ownership.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM detection.scenario_evaluation AS se
        LEFT JOIN transaction.transaction AS tx
            ON tx.transaction_id = se.transaction_id
        WHERE se.transaction_id IS NOT NULL
          AND (
                tx.transaction_id IS NULL
                OR tx.organization_id IS NULL
              )
    ) THEN
        RAISE EXCEPTION
            'V183 cannot resolve ScenarioEvaluation Transaction organizational scope';
    END IF;
END
$$;

-- Validate every referenced Customer before deriving ownership.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM detection.scenario_evaluation AS se
        LEFT JOIN customer.customer AS c
            ON c.customer_id = se.customer_id
        LEFT JOIN administration.tenant AS t
            ON t.tenant_id = c.tenant_id
        WHERE se.customer_id IS NOT NULL
          AND (
                c.customer_id IS NULL
                OR c.tenant_id IS NULL
                OR t.tenant_id IS NULL
                OR t.organization_id IS NULL
              )
    ) THEN
        RAISE EXCEPTION
            'V183 cannot resolve ScenarioEvaluation Customer organizational scope';
    END IF;
END
$$;

-- Transaction + Customer must resolve to the exact same scope.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM detection.scenario_evaluation AS se
        JOIN transaction.transaction AS tx
            ON tx.transaction_id = se.transaction_id
        JOIN customer.customer AS c
            ON c.customer_id = se.customer_id
        JOIN administration.tenant AS t
            ON t.tenant_id = c.tenant_id
        WHERE se.transaction_id IS NOT NULL
          AND se.customer_id IS NOT NULL
          AND (
                tx.organization_id IS DISTINCT FROM t.organization_id
                OR tx.tenant_id IS DISTINCT FROM c.tenant_id
              )
    ) THEN
        RAISE EXCEPTION
            'V183 detected ScenarioEvaluation Transaction/Customer scope mismatch';
    END IF;
END
$$;

-- Transaction is the ownership source when present.
UPDATE detection.scenario_evaluation AS se
SET
    organization_id = tx.organization_id,
    tenant_id = tx.tenant_id
FROM transaction.transaction AS tx
WHERE se.transaction_id = tx.transaction_id;

-- Customer is the ownership source only when Transaction is absent.
UPDATE detection.scenario_evaluation AS se
SET
    organization_id = t.organization_id,
    tenant_id = c.tenant_id
FROM customer.customer AS c
JOIN administration.tenant AS t
    ON t.tenant_id = c.tenant_id
WHERE se.transaction_id IS NULL
  AND se.customer_id = c.customer_id;

-- Historical rows without deterministic domain ownership fail closed.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM detection.scenario_evaluation
        WHERE organization_id IS NULL
    ) THEN
        RAISE EXCEPTION
            'V183 cannot resolve organization_id for one or more ScenarioEvaluation records';
    END IF;
END
$$;

-- Validate tenant / organization consistency before constraints.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM detection.scenario_evaluation AS se
        WHERE se.tenant_id IS NOT NULL
          AND NOT EXISTS (
                SELECT 1
                FROM administration.tenant AS t
                WHERE t.tenant_id = se.tenant_id
                  AND t.organization_id = se.organization_id
              )
    ) THEN
        RAISE EXCEPTION
            'V183 detected ScenarioEvaluation tenant_id inconsistent with organization_id';
    END IF;
END
$$;

ALTER TABLE detection.scenario_evaluation
    ALTER COLUMN organization_id SET NOT NULL;

ALTER TABLE detection.scenario_evaluation
    ADD CONSTRAINT fk_scenario_evaluation_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id);

ALTER TABLE detection.scenario_evaluation
    ADD CONSTRAINT fk_scenario_evaluation_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id);

CREATE INDEX idx_scenario_evaluation_organization_tenant
    ON detection.scenario_evaluation (
        organization_id,
        tenant_id
    );