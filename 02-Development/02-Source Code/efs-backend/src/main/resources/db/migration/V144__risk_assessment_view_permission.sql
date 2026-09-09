-- EFS Risk Assessment View Permission Contract v1
-- UC-029 - Risk Score Review

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'risk.assessment.view',
    'View Risk Assessment',
    'risk.assessment',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'risk.assessment.view'
);