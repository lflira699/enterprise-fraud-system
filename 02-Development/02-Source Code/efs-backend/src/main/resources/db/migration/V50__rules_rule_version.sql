-- EFS-DB-002
-- V50 - Rule Engine Rule Version
-- Controlled Physical Design Decision:
-- Completes physical typing for the approved rule_version structure.

CREATE TABLE rules.rule_version (
    rule_version_id UUID NOT NULL DEFAULT uuidv7(),
    rule_id UUID NOT NULL,
    version_number INTEGER NOT NULL,
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    publication_status VARCHAR(30) NOT NULL,
    change_summary TEXT,
    created_by UUID NOT NULL,
    approved_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_rule_version
        PRIMARY KEY (rule_version_id),

    CONSTRAINT fk_rule_version_rule
        FOREIGN KEY (rule_id)
        REFERENCES rules.rule (rule_id),

    CONSTRAINT fk_rule_version_created_by
        FOREIGN KEY (created_by)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT fk_rule_version_approved_by
        FOREIGN KEY (approved_by)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT uq_rule_version_number
        UNIQUE (rule_id, version_number)
);

CREATE INDEX idx_rule_version
    ON rules.rule_version (rule_id, version_number);

CREATE INDEX idx_rule_effective
    ON rules.rule_version (effective_from, effective_to);

CREATE INDEX idx_rule_version_status
    ON rules.rule_version (publication_status);