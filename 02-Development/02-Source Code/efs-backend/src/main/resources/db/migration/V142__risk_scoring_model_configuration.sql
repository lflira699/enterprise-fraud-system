-- EFS-DB-002
-- V142 - Risk Scoring Model Configuration
-- UC-028 - Risk Score Calculation
--
-- Global baseline configuration for EFS Risk Scoring Model 1.0.
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
        'EFS.RISK.ACTIVE_MODEL',
        '1.0',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.NAME',
        'EFS-RISK',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.SCORE.MIN',
        '0',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.SCORE.MAX',
        '100',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.0.FACTOR.RULES.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.FACTOR.RULES.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.0.FACTOR.BEHAVIORAL.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.FACTOR.BEHAVIORAL.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.0.FACTOR.CUSTOMER.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.FACTOR.CUSTOMER.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.0.FACTOR.GEOGRAPHIC.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.FACTOR.GEOGRAPHIC.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.0.FACTOR.DEVICE.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.FACTOR.DEVICE.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.MODEL.1.0.THRESHOLD.LOW.MIN',
        '0',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.THRESHOLD.MEDIUM.MIN',
        '20',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.THRESHOLD.HIGH.MIN',
        '40',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.THRESHOLD.VERY_HIGH.MIN',
        '60',
        'STRING'
    ),
    (
        'EFS.RISK.MODEL.1.0.THRESHOLD.CRITICAL.MIN',
        '80',
        'STRING'
    );