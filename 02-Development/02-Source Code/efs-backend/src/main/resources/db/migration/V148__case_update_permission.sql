-- EFS Case Update Permission Contract v1
-- UC-035 - Investigation Update

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'case.update',
    'Update Investigation Cases',
    'case',
    'update'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'case.update'
);
