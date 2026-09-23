-- EFS Relationship Analysis Security Contract v1
-- Relationship Analysis Authorized HTTP Boundary
-- No automatic role assignment.

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'relationship.analysis.view',
    'View Relationship Analyses',
    'relationship.analysis',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'relationship.analysis.view'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'relationship.analysis.create',
    'Create Relationship Analyses',
    'relationship.analysis',
    'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'relationship.analysis.create'
);