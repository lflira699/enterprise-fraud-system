-- EFS Report Generate Permission Contract v1
-- UC-041 - Report Generation

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'report.generate',
    'Generate Reports',
    'report',
    'generate'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'report.generate'
);
