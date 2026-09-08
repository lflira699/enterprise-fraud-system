-- EFS-DB-002
-- V140 - Rule Simulation Provenance
-- Controlled Physical Design Decision:
-- Distinguishes generic simulations from controlled UC-025 rule tests so
-- UC-026 publication can enforce simulation-before-publication without
-- trusting caller-supplied simulation metadata.

ALTER TABLE rules.rule_simulation
    ADD COLUMN simulation_source VARCHAR(30);

UPDATE rules.rule_simulation
SET simulation_source = 'GENERIC'
WHERE simulation_source IS NULL;

ALTER TABLE rules.rule_simulation
    ALTER COLUMN simulation_source SET NOT NULL,
    ALTER COLUMN simulation_source SET DEFAULT 'GENERIC';

ALTER TABLE rules.rule_simulation
    ADD CONSTRAINT ck_rule_simulation_source
    CHECK (
        simulation_source IN (
            'GENERIC',
            'CONTROLLED_RULE_TEST'
        )
    );

CREATE INDEX idx_rule_simulation_publish_eligibility
    ON rules.rule_simulation (
        entity_type,
        entity_id,
        simulation_source,
        simulation_status,
        completed_at
    );