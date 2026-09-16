-- EFS Report Export Permission Contract v1
-- UC-042 - Report Export

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'report.export',
    'Export Generated Reports',
    'report',
    'export'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'report.export'
);