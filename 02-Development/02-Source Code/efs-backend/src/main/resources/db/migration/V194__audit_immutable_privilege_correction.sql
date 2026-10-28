-- EFS Audit Privilege Correction
-- Forward-only correction approved under Architecture Freeze.
-- Reference: EFS-DB-002 Audit Aggregate / Least Privilege.
-- Historical migrations remain unchanged.

-- Revoke UPDATE on existing audit tables.
REVOKE UPDATE
ON ALL TABLES IN SCHEMA audit
FROM "efs_app";

-- Revoke UPDATE on future audit tables created by Flyway.
ALTER DEFAULT PRIVILEGES
FOR ROLE "efs_admin"
IN SCHEMA audit
REVOKE UPDATE ON TABLES
FROM "efs_app";
