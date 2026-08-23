-- EFS-DB-002 v2.0
-- V130 - Playbook Version

CREATE TABLE playbook.playbook_version (
    playbook_version_id UUID NOT NULL DEFAULT uuidv7(),
    playbook_id UUID NOT NULL,
    version_number INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_playbook_version
        PRIMARY KEY (playbook_version_id),

    CONSTRAINT fk_playbook_version_playbook
        FOREIGN KEY (playbook_id)
        REFERENCES playbook.playbook (playbook_id),

    CONSTRAINT uk_playbook_version
        UNIQUE (playbook_id, version_number),

    CONSTRAINT ck_playbook_version_number
        CHECK (version_number >= 1),

    CONSTRAINT ck_playbook_version_effective_period
        CHECK (
            effective_from IS NULL
            OR effective_to IS NULL
            OR effective_to >= effective_from
        )
);

CREATE INDEX idx_playbook_version_playbook
    ON playbook.playbook_version (playbook_id);

CREATE INDEX idx_playbook_version_status
    ON playbook.playbook_version (status);