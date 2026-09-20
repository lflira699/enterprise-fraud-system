-- EFS Evidence Create Permission Contract v1
-- UC-018 - Evidence Registration

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'evidence.create',
    'Create Evidence',
    'evidence',
    'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'evidence.create'
);
