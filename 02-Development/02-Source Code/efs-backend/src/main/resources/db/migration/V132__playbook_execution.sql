-- EFS-DB-002 v2.0
-- V132 - Playbook Execution

CREATE TABLE playbook.playbook_execution (
    playbook_execution_id UUID NOT NULL DEFAULT uuidv7(),
    playbook_version_id UUID NOT NULL,
    alert_id UUID,
    scenario_id UUID,
    status VARCHAR(30) NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_playbook_execution
        PRIMARY KEY (playbook_execution_id),

    CONSTRAINT fk_playbook_execution_version
        FOREIGN KEY (playbook_version_id)
        REFERENCES playbook.playbook_version (playbook_version_id),

    CONSTRAINT fk_playbook_execution_alert
        FOREIGN KEY (alert_id)
        REFERENCES alert.alert (alert_id),

    CONSTRAINT fk_playbook_execution_scenario
        FOREIGN KEY (scenario_id)
        REFERENCES detection.scenario (scenario_id),

    CONSTRAINT ck_playbook_execution_period
        CHECK (
            completed_at IS NULL
            OR completed_at >= started_at
        )
);

CREATE INDEX idx_playbook_execution_version
    ON playbook.playbook_execution (playbook_version_id);

CREATE INDEX idx_playbook_execution_alert
    ON playbook.playbook_execution (alert_id);

CREATE INDEX idx_playbook_execution_scenario
    ON playbook.playbook_execution (scenario_id);

CREATE INDEX idx_playbook_execution_status
    ON playbook.playbook_execution (status);

CREATE INDEX idx_playbook_execution_started_at
    ON playbook.playbook_execution (started_at);