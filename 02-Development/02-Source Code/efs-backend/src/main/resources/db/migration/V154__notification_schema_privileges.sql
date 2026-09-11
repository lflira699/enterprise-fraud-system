-- EFS-DB-002 v2.1
-- V154 - Notification Schema Privileges
-- UC-038 - Notification Delivery
-- COMP-013 - Notification Service

GRANT USAGE ON SCHEMA notification TO efs_app;

GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA notification
TO efs_app;

GRANT USAGE, SELECT
ON ALL SEQUENCES IN SCHEMA notification
TO efs_app;
