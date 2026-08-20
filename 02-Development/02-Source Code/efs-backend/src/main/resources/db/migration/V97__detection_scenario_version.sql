-- EFS-DB-002
-- V97 - Detection Engine Scenario Version

CREATE TABLE detection.scenario_version (
    scenario_version_id UUID NOT NULL DEFAULT uuidv7(),
    scenario_id UUID NOT NULL,
    version_number INTEGER NOT NULL,
    version_status VARCHAR(30) NOT NULL,
    correlation_window_seconds BIGINT NOT NULL,
    maximum_processing_time_ms INTEGER,
    minimum_events INTEGER,
    minimum_confidence NUMERIC(8,4),
    activation_mode VARCHAR(30) NOT NULL,
    configuration JSONB,
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_scenario_version
        PRIMARY KEY (scenario_version_id),

    CONSTRAINT fk_detection_scenario_version_scenario
        FOREIGN KEY (scenario_id)
        REFERENCES detection.scenario (scenario_id),

    CONSTRAINT uk_detection_scenario_version
        UNIQUE (scenario_id, version_number)
);

CREATE INDEX idx_detection_scenario_version_scenario
    ON detection.scenario_version (scenario_id);

CREATE INDEX idx_detection_scenario_version_status
    ON detection.scenario_version (version_status);

CREATE INDEX idx_detection_scenario_version_effective
    ON detection.scenario_version (effective_from, effective_to);

CREATE INDEX idx_detection_scenario_version_configuration
    ON detection.scenario_version USING GIN (configuration);