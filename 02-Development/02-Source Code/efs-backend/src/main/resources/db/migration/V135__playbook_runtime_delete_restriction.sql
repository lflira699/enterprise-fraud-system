-- EFS-DB-002 v2.0
-- V135 - Playbook Runtime Delete Restriction

REVOKE DELETE
ON ALL TABLES IN SCHEMA playbook
FROM efs_app;