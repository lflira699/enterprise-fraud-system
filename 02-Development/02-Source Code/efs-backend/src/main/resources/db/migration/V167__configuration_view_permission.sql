-- EFS UC-044
-- Configuration Governance Contract v1

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'configuration.view',
    'View System Configuration',
    'configuration',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'configuration.view'
);