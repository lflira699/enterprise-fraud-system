-- EFS Network Analysis Security Contract v1
-- Network Analysis Authorized HTTP Boundary
-- No automatic role assignment.

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'network.analysis.view',
    'View Network Analyses',
    'network.analysis',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'network.analysis.view'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'network.analysis.create',
    'Create Network Analyses',
    'network.analysis',
    'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'network.analysis.create'
);