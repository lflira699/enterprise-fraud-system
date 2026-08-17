-- EFS-DB-002
-- V40 - Case Management Case Task

CREATE TABLE case_management.case_task (
    task_id UUID NOT NULL DEFAULT uuidv7(),
    case_id UUID NOT NULL,
    task_name VARCHAR(150) NOT NULL,
    task_description TEXT,
    assigned_to UUID,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    due_date TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_case_task
        PRIMARY KEY (task_id),

    CONSTRAINT fk_case_task_case
        FOREIGN KEY (case_id)
        REFERENCES case_management.case (case_id),

    CONSTRAINT fk_case_task_assigned_to
        FOREIGN KEY (assigned_to)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_task_case
    ON case_management.case_task (case_id);

CREATE INDEX idx_task_status
    ON case_management.case_task (status);

CREATE INDEX idx_task_assigned
    ON case_management.case_task (assigned_to);

CREATE INDEX idx_task_due_date
    ON case_management.case_task (due_date);