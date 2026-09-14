-- EFS Report Generation Maximum Records Configuration v1
-- UC-041 - Report Generation

INSERT INTO administration.system_configuration (
    configuration_key,
    configuration_value,
    configuration_type,
    tenant_id,
    organization_id,
    encrypted,
    updated_by,
    updated_at
)
SELECT
    'EFS.REPORT.MAX_RECORDS',
    '100',
    'INTEGER',
    NULL,
    NULL,
    FALSE,
    NULL,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.system_configuration
    WHERE configuration_key = 'EFS.REPORT.MAX_RECORDS'
      AND organization_id IS NULL
      AND tenant_id IS NULL
);
