-- EFS-DB-002
-- V139 - Rule Version Snapshot Reconciliation
-- Controlled Physical Design Reconciliation:
-- Preserves the functional Rule snapshot within each RuleVersion.

ALTER TABLE rules.rule_version
    ADD COLUMN rule_name VARCHAR(200) NOT NULL,
    ADD COLUMN description TEXT,
    ADD COLUMN category VARCHAR(50) NOT NULL,
    ADD COLUMN severity VARCHAR(20) NOT NULL,
    ADD COLUMN priority SMALLINT NOT NULL,
    ADD COLUMN owner_team VARCHAR(100);