-- EFS Case View Permission Contract v1
-- UC-033 - Investigation Search

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'case.view',
    'View Investigation Cases',
    'case',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'case.view'
);