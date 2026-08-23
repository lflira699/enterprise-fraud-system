-- EFS-DB-002 v2.0
-- V133 - Playbook Execution Step

CREATE TABLE playbook.playbook_execution_step (
    playbook_execution_step_id UUID NOT NULL DEFAULT uuidv7(),
    playbook_execution_id UUID NOT NULL,
    playbook_step_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    result TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_playbook_execution_step
        PRIMARY KEY (playbook_execution_step_id),

    CONSTRAINT fk_playbook_execution_step_execution
        FOREIGN KEY (playbook_execution_id)
        REFERENCES playbook.playbook_execution (playbook_execution_id),

    CONSTRAINT fk_playbook_execution_step_step
        FOREIGN KEY (playbook_step_id)
        REFERENCES playbook.playbook_step (playbook_step_id),

    CONSTRAINT uk_playbook_execution_step
        UNIQUE (playbook_execution_id, playbook_step_id),

    CONSTRAINT ck_playbook_execution_step_period
        CHECK (
            started_at IS NULL
            OR completed_at IS NULL
            OR completed_at >= started_at
        )
);

CREATE INDEX idx_playbook_execution_step_execution
    ON playbook.playbook_execution_step (playbook_execution_id);

CREATE INDEX idx_playbook_execution_step_step
    ON playbook.playbook_execution_step (playbook_step_id);

CREATE INDEX idx_playbook_execution_step_status
    ON playbook.playbook_execution_step (status);