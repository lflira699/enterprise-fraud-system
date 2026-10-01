-- EFS UC-006
-- Permission Management Binding v1
-- Administrative permission catalog entries.
-- No automatic role assignment.

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'permission.view',
    'View Permission Catalog',
    'permission',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'permission.view'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'permission.manage',
    'Manage Permission Catalog',
    'permission',
    'manage'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'permission.manage'
);