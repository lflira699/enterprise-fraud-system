-- EFS-DB-002 v2.0
-- V129 - Playbook

CREATE TABLE playbook.playbook (
    playbook_id UUID NOT NULL DEFAULT uuidv7(),
    playbook_code VARCHAR(60) NOT NULL,
    playbook_name VARCHAR(180) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_playbook
        PRIMARY KEY (playbook_id),

    CONSTRAINT uk_playbook_code
        UNIQUE (playbook_code)
);

CREATE INDEX idx_playbook_status
    ON playbook.playbook (status);