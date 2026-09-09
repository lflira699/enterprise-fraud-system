-- EFS Customer Risk Scoring Model 1.0
-- UC-030 - Customer Risk Assessment
--
-- Global baseline configuration.
-- Customer Risk Model v1 resolves exclusively from Global scope.
--
-- Canonical EFS risk taxonomy:
--   VERY_LOW
--   LOW
--   MEDIUM
--   HIGH
--   CRITICAL
--
-- All values are stored as STRING and parsed by the Risk Engine.

INSERT INTO administration.system_configuration (
    configuration_key,
    configuration_value,
    configuration_type
)
VALUES
    (
        'EFS.RISK.CUSTOMER.ACTIVE_MODEL',
        '1.0',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.NAME',
        'EFS-CUSTOMER-RISK',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.SCORE.MIN',
        '0',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.SCORE.MAX',
        '100',
        'STRING'
    ),

    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.BEHAVIOR.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.BEHAVIOR.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.FRAUD.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.FRAUD.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.AML.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.AML.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.KYC.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.KYC.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.DEVICE.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.DEVICE.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.SANCTIONS.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.SANCTIONS.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.PEP.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.PEP.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.WATCHLIST.ENABLED',
        'true',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.WATCHLIST.WEIGHT',
        '1',
        'STRING'
    ),

    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.THRESHOLD.VERY_LOW.MIN',
        '0',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.THRESHOLD.LOW.MIN',
        '20',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.THRESHOLD.MEDIUM.MIN',
        '40',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.THRESHOLD.HIGH.MIN',
        '60',
        'STRING'
    ),
    (
        'EFS.RISK.CUSTOMER.MODEL.1.0.THRESHOLD.CRITICAL.MIN',
        '80',
        'STRING'
    );