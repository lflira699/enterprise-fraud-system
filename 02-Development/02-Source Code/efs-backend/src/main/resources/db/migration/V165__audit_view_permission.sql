-- EFS Audit Log Review Permission Contract v1
-- UC-043 - Audit Log Review

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'audit.view',
    'View Audit Logs',
    'audit',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'audit.view'
);