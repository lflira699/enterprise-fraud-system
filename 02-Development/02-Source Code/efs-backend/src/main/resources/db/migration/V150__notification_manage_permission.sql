-- EFS Notification Management Permission Contract v1
-- UC-037 - Notification Management

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'notification.manage',
    'Manage Notifications',
    'notification',
    'manage'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'notification.manage'
);