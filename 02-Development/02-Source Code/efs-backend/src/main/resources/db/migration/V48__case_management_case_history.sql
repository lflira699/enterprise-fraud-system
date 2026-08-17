-- EFS-DB-002
-- V48 - Case Management Case History
-- Controlled Physical Design Decision:
-- General immutable trace of case changes.
-- Status transitions remain in case_status_history.

CREATE TABLE case_management.case_history (
    history_id UUID NOT NULL DEFAULT uuidv7(),
    case_id UUID NOT NULL,
    event_type VARCHAR(60) NOT NULL,
    event_description TEXT NOT NULL,
    previous_value TEXT,
    new_value TEXT,
    changed_by UUID NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_case_history
        PRIMARY KEY (history_id),

    CONSTRAINT fk_case_history_case
        FOREIGN KEY (case_id)
        REFERENCES case_management.case (case_id),

    CONSTRAINT fk_case_history_changed_by
        FOREIGN KEY (changed_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_case_history_case
    ON case_management.case_history (case_id);

CREATE INDEX idx_case_history_event
    ON case_management.case_history (event_type);

CREATE INDEX idx_case_history_user
    ON case_management.case_history (changed_by);

CREATE INDEX idx_case_history_date
    ON case_management.case_history (changed_at);