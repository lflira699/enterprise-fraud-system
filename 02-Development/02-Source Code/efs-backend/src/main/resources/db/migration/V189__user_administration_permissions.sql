-- EFS UC-004 / UC-008
-- User Administration Authorization Catalog Reconciliation
-- No automatic role assignment.

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'user.view',
    'View Users',
    'user',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'user.view'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'user.manage',
    'Manage Users',
    'user',
    'manage'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'user.manage'
);