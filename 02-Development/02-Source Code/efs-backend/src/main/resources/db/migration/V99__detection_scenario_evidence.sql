-- EFS-DB-002
-- V99 - Detection Engine Scenario Evidence

CREATE TABLE detection.scenario_evidence (
    evidence_id UUID NOT NULL DEFAULT uuidv7(),
    scenario_version_id UUID NOT NULL,
    evidence_type VARCHAR(40) NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    source_reference VARCHAR(250),
    evidence_value JSONB,
    evidence_summary TEXT,
    confidence NUMERIC(8,4),
    observed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_detection_scenario_evidence
        PRIMARY KEY (evidence_id),

    CONSTRAINT fk_detection_scenario_evidence_version
        FOREIGN KEY (scenario_version_id)
        REFERENCES detection.scenario_version (scenario_version_id)
);

CREATE INDEX idx_detection_scenario_evidence_version
    ON detection.scenario_evidence (scenario_version_id);

CREATE INDEX idx_detection_scenario_evidence_type
    ON detection.scenario_evidence (evidence_type);

CREATE INDEX idx_detection_scenario_evidence_source
    ON detection.scenario_evidence (source_type);

CREATE INDEX idx_detection_scenario_evidence_observed
    ON detection.scenario_evidence (observed_at);

CREATE INDEX gin_detection_scenario_evidence_value
    ON detection.scenario_evidence USING GIN (evidence_value);