-- EFS-DB-002
-- V43 - Case Management Case Status History

CREATE TABLE case_management.case_status_history (
    history_id UUID NOT NULL DEFAULT uuidv7(),
    case_id UUID NOT NULL,
    previous_status VARCHAR(30),
    current_status VARCHAR(30) NOT NULL,
    change_reason TEXT,
    changed_by UUID NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_case_status_history
        PRIMARY KEY (history_id),

    CONSTRAINT fk_case_status_history_case
        FOREIGN KEY (case_id)
        REFERENCES case_management.case (case_id),

    CONSTRAINT fk_case_status_history_changed_by
        FOREIGN KEY (changed_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_case_status_history_status
    ON case_management.case_status_history (case_id, current_status);

CREATE INDEX idx_case_status_date
    ON case_management.case_status_history (changed_at);

CREATE INDEX idx_case_status_user
    ON case_management.case_status_history (changed_by);