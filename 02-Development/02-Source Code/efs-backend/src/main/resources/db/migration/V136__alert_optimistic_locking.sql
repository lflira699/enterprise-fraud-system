-- EFS-DB-002
-- V136 - Alert Optimistic Locking

ALTER TABLE alert.alert
    ADD COLUMN record_version INTEGER NOT NULL DEFAULT 1;