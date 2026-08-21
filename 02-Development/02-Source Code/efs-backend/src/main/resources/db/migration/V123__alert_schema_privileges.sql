-- EFS-DB-002
-- V123 - Alert Schema Privileges

-- Runtime application account: efs_app
-- Migration/administration account: efs_admin

GRANT USAGE ON SCHEMA alert TO efs_app;

GRANT SELECT, INSERT, UPDATE
    ON ALL TABLES IN SCHEMA alert
    TO efs_app;

ALTER DEFAULT PRIVILEGES FOR ROLE efs_admin
    IN SCHEMA alert
    GRANT SELECT, INSERT, UPDATE ON TABLES TO efs_app;