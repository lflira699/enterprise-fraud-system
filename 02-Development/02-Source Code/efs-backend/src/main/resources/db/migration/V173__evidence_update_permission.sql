-- EFS Evidence Update Permission Contract v1
-- UC-020 - Evidence Update

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'evidence.update',
    'Update Evidence',
    'evidence',
    'update'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'evidence.update'
);
