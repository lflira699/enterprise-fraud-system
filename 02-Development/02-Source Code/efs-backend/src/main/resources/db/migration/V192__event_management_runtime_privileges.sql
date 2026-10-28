-- EFS-DB-002
-- V192 - Event Management Runtime Privileges
-- Runtime application account: efs_app
-- Migration/administration account: efs_admin

GRANT USAGE
ON SCHEMA event_management
TO efs_app;

GRANT SELECT, INSERT
ON TABLE event_management.fraud_event
TO efs_app;