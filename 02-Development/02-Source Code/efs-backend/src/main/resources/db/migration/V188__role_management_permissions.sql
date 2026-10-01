-- EFS UC-005
-- Role Authorization Catalog Reconciliation v1
-- Materializes authorization permissions already bound by RoleManagementService.
-- No automatic role assignment.

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'role.view',
    'View Roles',
    'role',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'role.view'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'role.manage',
    'Manage Roles',
    'role',
    'manage'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'role.manage'
);