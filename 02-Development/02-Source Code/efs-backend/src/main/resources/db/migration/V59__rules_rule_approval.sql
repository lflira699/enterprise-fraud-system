-- EFS-DB-002
-- V59 - Rule Engine Rule Approval

CREATE TABLE rules.rule_approval (
    approval_id UUID NOT NULL DEFAULT uuidv7(),
    entity_type VARCHAR(30) NOT NULL,
    entity_id UUID NOT NULL,
    approval_status VARCHAR(30) NOT NULL,
    submitted_by UUID NOT NULL,
    submitted_at TIMESTAMP NOT NULL,
    reviewed_by UUID,
    reviewed_at TIMESTAMP,
    decision_comment TEXT,
    approval_level SMALLINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_rule_approval
        PRIMARY KEY (approval_id),

    CONSTRAINT fk_rule_approval_submitted_by
        FOREIGN KEY (submitted_by)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT fk_rule_approval_reviewed_by
        FOREIGN KEY (reviewed_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_approval_entity
    ON rules.rule_approval (entity_type, entity_id);

CREATE INDEX idx_approval_status
    ON rules.rule_approval (approval_status);

CREATE INDEX idx_approval_submitted_by
    ON rules.rule_approval (submitted_by);

CREATE INDEX idx_approval_reviewed_by
    ON rules.rule_approval (reviewed_by);