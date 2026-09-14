-- EFS Dashboard View Permission Contract v1
-- UC-039 - Dashboard View

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'dashboard.view',
    'View Dashboard',
    'dashboard',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'dashboard.view'
);
