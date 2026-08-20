-- EFS-DB-002
-- V116 - Application Database Privileges

-- Runtime application account: efs_app
-- Migration/administration account: efs_admin
--
-- The application account receives only the privileges required
-- by the current EFS runtime persistence model.

GRANT USAGE ON SCHEMA customer TO efs_app;
GRANT USAGE ON SCHEMA transaction TO efs_app;
GRANT USAGE ON SCHEMA rules TO efs_app;
GRANT USAGE ON SCHEMA detection TO efs_app;
GRANT USAGE ON SCHEMA case_management TO efs_app;
GRANT USAGE ON SCHEMA administration TO efs_app;
GRANT USAGE ON SCHEMA audit TO efs_app;
GRANT USAGE ON SCHEMA catalog TO efs_app;
GRANT USAGE ON SCHEMA integration TO efs_app;
GRANT USAGE ON SCHEMA reporting TO efs_app;

-- Existing application tables.

GRANT SELECT, INSERT, UPDATE
    ON ALL TABLES IN SCHEMA customer
    TO efs_app;

GRANT SELECT, INSERT, UPDATE
    ON ALL TABLES IN SCHEMA transaction
    TO efs_app;

GRANT SELECT, INSERT, UPDATE
    ON ALL TABLES IN SCHEMA rules
    TO efs_app;

GRANT SELECT, INSERT, UPDATE
    ON ALL TABLES IN SCHEMA detection
    TO efs_app;

GRANT SELECT, INSERT, UPDATE
    ON ALL TABLES IN SCHEMA case_management
    TO efs_app;

GRANT SELECT, INSERT, UPDATE
    ON ALL TABLES IN SCHEMA administration
    TO efs_app;

GRANT SELECT, INSERT, UPDATE
    ON ALL TABLES IN SCHEMA audit
    TO efs_app;

GRANT SELECT, INSERT, UPDATE
    ON ALL TABLES IN SCHEMA catalog
    TO efs_app;

GRANT SELECT, INSERT, UPDATE
    ON ALL TABLES IN SCHEMA integration
    TO efs_app;

GRANT SELECT, INSERT, UPDATE
    ON ALL TABLES IN SCHEMA reporting
    TO efs_app;

-- Explicit physical delete currently required by CustomerDocumentService.

GRANT DELETE
    ON TABLE customer.customer_document
    TO efs_app;

-- Future tables created by the Flyway owner automatically receive
-- the standard runtime privileges.
-- DELETE remains explicit and must be approved per table.

ALTER DEFAULT PRIVILEGES FOR ROLE efs_admin
    IN SCHEMA customer
    GRANT SELECT, INSERT, UPDATE ON TABLES TO efs_app;

ALTER DEFAULT PRIVILEGES FOR ROLE efs_admin
    IN SCHEMA transaction
    GRANT SELECT, INSERT, UPDATE ON TABLES TO efs_app;

ALTER DEFAULT PRIVILEGES FOR ROLE efs_admin
    IN SCHEMA rules
    GRANT SELECT, INSERT, UPDATE ON TABLES TO efs_app;

ALTER DEFAULT PRIVILEGES FOR ROLE efs_admin
    IN SCHEMA detection
    GRANT SELECT, INSERT, UPDATE ON TABLES TO efs_app;

ALTER DEFAULT PRIVILEGES FOR ROLE efs_admin
    IN SCHEMA case_management
    GRANT SELECT, INSERT, UPDATE ON TABLES TO efs_app;

ALTER DEFAULT PRIVILEGES FOR ROLE efs_admin
    IN SCHEMA administration
    GRANT SELECT, INSERT, UPDATE ON TABLES TO efs_app;

ALTER DEFAULT PRIVILEGES FOR ROLE efs_admin
    IN SCHEMA audit
    GRANT SELECT, INSERT, UPDATE ON TABLES TO efs_app;

ALTER DEFAULT PRIVILEGES FOR ROLE efs_admin
    IN SCHEMA catalog
    GRANT SELECT, INSERT, UPDATE ON TABLES TO efs_app;

ALTER DEFAULT PRIVILEGES FOR ROLE efs_admin
    IN SCHEMA integration
    GRANT SELECT, INSERT, UPDATE ON TABLES TO efs_app;

ALTER DEFAULT PRIVILEGES FOR ROLE efs_admin
    IN SCHEMA reporting
    GRANT SELECT, INSERT, UPDATE ON TABLES TO efs_app;