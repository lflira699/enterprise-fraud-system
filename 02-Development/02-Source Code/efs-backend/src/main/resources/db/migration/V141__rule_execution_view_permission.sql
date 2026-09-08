-- EFS Rule Execution View Permission Contract v1
-- UC-027 - Rule Execution History

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'rule.execution.view',
    'View Rule Execution History',
    'rule.execution',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'rule.execution.view'
);