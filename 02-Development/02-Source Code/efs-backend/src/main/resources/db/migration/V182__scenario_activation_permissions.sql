-- EFS Scenario Activation Security Contract v1.1
-- HTTP authorization permissions only
-- No automatic role assignment

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'scenario.activation.view',
    'View Scenario Activations',
    'scenario.activation',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'scenario.activation.view'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'scenario.activation.create',
    'Create Scenario Activations',
    'scenario.activation',
    'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'scenario.activation.create'
);