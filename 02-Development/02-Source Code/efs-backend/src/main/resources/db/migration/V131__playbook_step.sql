-- EFS-DB-002 v2.0
-- V131 - Playbook Step

CREATE TABLE playbook.playbook_step (
    playbook_step_id UUID NOT NULL DEFAULT uuidv7(),
    playbook_version_id UUID NOT NULL,
    step_order INTEGER NOT NULL,
    step_name VARCHAR(180) NOT NULL,
    description TEXT,
    expected_result TEXT,
    expected_duration_minutes INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_playbook_step
        PRIMARY KEY (playbook_step_id),

    CONSTRAINT fk_playbook_step_version
        FOREIGN KEY (playbook_version_id)
        REFERENCES playbook.playbook_version (playbook_version_id),

    CONSTRAINT uk_playbook_step_order
        UNIQUE (playbook_version_id, step_order),

    CONSTRAINT ck_playbook_step_order
        CHECK (step_order > 0),

    CONSTRAINT ck_playbook_step_duration
        CHECK (
            expected_duration_minutes IS NULL
            OR expected_duration_minutes >= 0
        )
);

CREATE INDEX idx_playbook_step_version
    ON playbook.playbook_step (playbook_version_id);