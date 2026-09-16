-- EFS UC-044
-- Configuration Governance Contract v1

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'configuration.approve',
    'Approve System Configuration',
    'configuration',
    'approve'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'configuration.approve'
);