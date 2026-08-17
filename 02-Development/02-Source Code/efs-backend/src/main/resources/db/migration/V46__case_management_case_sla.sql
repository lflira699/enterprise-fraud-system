-- EFS-DB-002
-- V46 - Case Management Case SLA

CREATE TABLE case_management.case_sla (
    sla_id UUID NOT NULL DEFAULT uuidv7(),
    case_id UUID NOT NULL,
    sla_type VARCHAR(40) NOT NULL,
    target_minutes INTEGER NOT NULL,
    elapsed_minutes INTEGER NOT NULL DEFAULT 0,
    deadline TIMESTAMP NOT NULL,
    breached BOOLEAN NOT NULL DEFAULT FALSE,
    breach_reason TEXT,
    calculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_case_sla
        PRIMARY KEY (sla_id),

    CONSTRAINT fk_case_sla_case
        FOREIGN KEY (case_id)
        REFERENCES case_management.case (case_id)
);

CREATE INDEX idx_sla_case
    ON case_management.case_sla (case_id);

CREATE INDEX idx_sla_status
    ON case_management.case_sla (breached);

CREATE INDEX idx_sla_deadline
    ON case_management.case_sla (deadline);