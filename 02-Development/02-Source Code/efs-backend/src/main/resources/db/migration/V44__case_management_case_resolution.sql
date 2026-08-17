-- EFS-DB-002
-- V44 - Case Management Case Resolution

CREATE TABLE case_management.case_resolution (
    resolution_id UUID NOT NULL DEFAULT uuidv7(),
    case_id UUID NOT NULL,
    resolution_type VARCHAR(40) NOT NULL,
    resolution_summary TEXT NOT NULL,
    economic_impact NUMERIC(18,2),
    currency_code CHAR(3),
    resolved_by UUID NOT NULL,
    resolved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_by UUID,

    CONSTRAINT pk_case_resolution
        PRIMARY KEY (resolution_id),

    CONSTRAINT fk_case_resolution_case
        FOREIGN KEY (case_id)
        REFERENCES case_management.case (case_id),

    CONSTRAINT fk_case_resolution_resolved_by
        FOREIGN KEY (resolved_by)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT fk_case_resolution_approved_by
        FOREIGN KEY (approved_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_resolution_case
    ON case_management.case_resolution (case_id);

CREATE INDEX idx_resolution_type
    ON case_management.case_resolution (resolution_type);

CREATE INDEX idx_resolution_date
    ON case_management.case_resolution (resolved_at);