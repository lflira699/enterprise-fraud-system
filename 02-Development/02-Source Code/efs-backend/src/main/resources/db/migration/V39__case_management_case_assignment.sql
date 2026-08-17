-- EFS-DB-002
-- V39 - Case Management Case Assignment

CREATE TABLE case_management.case_assignment (
    assignment_id UUID NOT NULL DEFAULT uuidv7(),
    case_id UUID NOT NULL,
    assigned_from UUID,
    assigned_to UUID NOT NULL,
    assigned_team VARCHAR(100),
    assignment_reason TEXT,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP,
    released_at TIMESTAMP,

    CONSTRAINT pk_case_assignment
        PRIMARY KEY (assignment_id),

    CONSTRAINT fk_case_assignment_case
        FOREIGN KEY (case_id)
        REFERENCES case_management.case (case_id),

    CONSTRAINT fk_case_assignment_from
        FOREIGN KEY (assigned_from)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT fk_case_assignment_to
        FOREIGN KEY (assigned_to)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_assignment_case
    ON case_management.case_assignment (case_id);

CREATE INDEX idx_assignment_user
    ON case_management.case_assignment (assigned_to);

CREATE INDEX idx_assignment_team
    ON case_management.case_assignment (assigned_team);

CREATE INDEX idx_assignment_date
    ON case_management.case_assignment (assigned_at);