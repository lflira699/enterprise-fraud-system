-- EFS Risk Scoring Model 1.1
-- UC-028 - Risk Score Calculation
--
-- Canonical EFS risk taxonomy:
--   VERY_LOW
--   LOW
--   MEDIUM
--   HIGH
--   CRITICAL
--
-- Risk Model 1.0 remains unchanged for historical traceability.
--
-- Global baseline configuration.
-- Scope resolution remains:
--   1. Organization + Tenant
--   2. Organization
--   3. Global
--
-- All values are stored as STRING and parsed by the Risk Engine.

INSERT INTO administration.system_configuration (
    configuration_key,
    configuration_value,
    configuration_type
)
VALUES
    (
        'EFS.RISK.MODEL.1.1.NAME',
        'EFS-RISK',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.1.SCORE.MIN',
        '0',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.1.SCORE.MAX',
        '100',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.1.FACTOR.RULES.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.1.FACTOR.RULES.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.1.FACTOR.BEHAVIORAL.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.1.FACTOR.BEHAVIORAL.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.1.FACTOR.CUSTOMER.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.1.FACTOR.CUSTOMER.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.1.FACTOR.GEOGRAPHIC.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.1.FACTOR.GEOGRAPHIC.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.1.FACTOR.DEVICE.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.1.FACTOR.DEVICE.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.1.THRESHOLD.VERY_LOW.MIN',
        '0',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.1.THRESHOLD.LOW.MIN',
        '20',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.1.THRESHOLD.MEDIUM.MIN',
        '40',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.1.THRESHOLD.HIGH.MIN',
        '60',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.1.THRESHOLD.CRITICAL.MIN',
        '80',
        'STRING'
    );

UPDATE administration.system_configuration
SET configuration_value = '1.1'
WHERE configuration_key = 'EFS.RISK.ACTIVE_MODEL'
  AND organization_id IS NULL
  AND tenant_id IS NULL;