-- EFS UC-045
-- System Health Monitoring Contract v1

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'health.view',
    'View System Health',
    'health',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'health.view'
);