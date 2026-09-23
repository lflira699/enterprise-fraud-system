-- EFS Device Analysis Security Contract v1
-- Device Analysis Authorized HTTP Boundary
-- No automatic role assignment.

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'device.analysis.view',
    'View Device Analyses',
    'device.analysis',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'device.analysis.view'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'device.analysis.create',
    'Create Device Analyses',
    'device.analysis',
    'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'device.analysis.create'
);