-- EFS Evidence View Permission Contract v1
-- UC-019 - Evidence Review

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'evidence.view',
    'View Evidence',
    'evidence',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'evidence.view'
);
