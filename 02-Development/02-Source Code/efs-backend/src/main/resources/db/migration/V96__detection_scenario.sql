-- EFS-DB-002
-- V96 - Detection Engine Scenario

CREATE TABLE detection.scenario (
    scenario_id UUID NOT NULL DEFAULT uuidv7(),
    scenario_code VARCHAR(60) NOT NULL,
    scenario_name VARCHAR(180) NOT NULL,
    objective TEXT,
    category VARCHAR(50) NOT NULL,
    scenario_type VARCHAR(40) NOT NULL,
    description TEXT,
    priority SMALLINT NOT NULL,
    owner_team VARCHAR(100),
    current_version INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_scenario
        PRIMARY KEY (scenario_id),

    CONSTRAINT uk_detection_scenario_code
        UNIQUE (scenario_code)
);

CREATE INDEX idx_detection_scenario_code
    ON detection.scenario (scenario_code);

CREATE INDEX idx_detection_scenario_category
    ON detection.scenario (category);

CREATE INDEX idx_detection_scenario_type
    ON detection.scenario (scenario_type);

CREATE INDEX idx_detection_scenario_status
    ON detection.scenario (status);

CREATE INDEX idx_detection_scenario_priority
    ON detection.scenario (priority);

CREATE INDEX idx_detection_scenario_effective
    ON detection.scenario (effective_from, effective_to);
