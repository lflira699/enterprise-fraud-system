-- EFS Evidence Delete Permission Contract v1
-- UC-021 - Evidence Removal

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'evidence.delete',
    'Delete Evidence',
    'evidence',
    'delete'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'evidence.delete'
);
