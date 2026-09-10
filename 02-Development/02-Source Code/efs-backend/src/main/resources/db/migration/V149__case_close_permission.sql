-- EFS Case Closure Permission Contract v1
-- UC-036 - Investigation Closure

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'case.close',
    'Close Investigation Cases',
    'case',
    'close'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'case.close'
);
