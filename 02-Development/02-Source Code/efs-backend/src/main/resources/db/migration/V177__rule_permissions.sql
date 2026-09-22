-- EFS Rule Security Contract v1
-- Rule Definition Authorized HTTP Boundary
-- No automatic role assignment.

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'rule.view',
    'View Rules',
    'rule',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'rule.view'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'rule.create',
    'Create Rules',
    'rule',
    'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'rule.create'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'rule.update',
    'Update Rules',
    'rule',
    'update'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'rule.update'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'rule.activate',
    'Activate Rules',
    'rule',
    'activate'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'rule.activate'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'rule.deactivate',
    'Deactivate Rules',
    'rule',
    'deactivate'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'rule.deactivate'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'rule.test',
    'Test Rules',
    'rule',
    'test'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'rule.test'
);