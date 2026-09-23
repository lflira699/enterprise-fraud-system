-- EFS Detection Scenario Security Contract v1
-- Detection Scenario Authorized HTTP Boundary
-- No automatic role assignment.

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'scenario.view',
    'View Detection Scenarios',
    'scenario',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'scenario.view'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'scenario.create',
    'Create Detection Scenarios',
    'scenario',
    'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'scenario.create'
);