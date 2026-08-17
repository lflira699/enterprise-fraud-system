-- EFS-DB-002
-- V45 - Case Management Case Escalation

CREATE TABLE case_management.case_escalation (
    escalation_id UUID NOT NULL DEFAULT uuidv7(),
    case_id UUID NOT NULL,
    escalation_level VARCHAR(40) NOT NULL,
    from_team VARCHAR(100),
    to_team VARCHAR(100) NOT NULL,
    escalation_reason TEXT NOT NULL,
    escalated_by UUID NOT NULL,
    escalated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,

    CONSTRAINT pk_case_escalation
        PRIMARY KEY (escalation_id),

    CONSTRAINT fk_case_escalation_case
        FOREIGN KEY (case_id)
        REFERENCES case_management.case (case_id),

    CONSTRAINT fk_case_escalation_escalated_by
        FOREIGN KEY (escalated_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_escalation_case
    ON case_management.case_escalation (case_id);

CREATE INDEX idx_escalation_level
    ON case_management.case_escalation (escalation_level);

CREATE INDEX idx_escalation_date
    ON case_management.case_escalation (escalated_at);