-- EFS-DB-002
-- V111 - Detection Scenario Repository Alignment

-- Align the original detection.scenario structure created by V96
-- with the current Detection Scenario repository model.

-- Remove indexes associated exclusively with the legacy V96 structure.

DROP INDEX IF EXISTS detection.idx_detection_scenario_type;
DROP INDEX IF EXISTS detection.idx_detection_scenario_priority;
DROP INDEX IF EXISTS detection.idx_detection_scenario_effective;

-- Remove the legacy unique constraint on scenario_code.
-- The current model versions scenarios by scenario_code + version.

ALTER TABLE detection.scenario
    DROP CONSTRAINT IF EXISTS uk_detection_scenario_code;

-- Remove legacy columns not present in the current repository model.

ALTER TABLE detection.scenario
    DROP COLUMN IF EXISTS scenario_type,
    DROP COLUMN IF EXISTS priority,
    DROP COLUMN IF EXISTS owner_team,
    DROP COLUMN IF EXISTS current_version,
    DROP COLUMN IF EXISTS effective_from,
    DROP COLUMN IF EXISTS effective_to;

-- Align existing columns with the current repository model.

ALTER TABLE detection.scenario
    ALTER COLUMN scenario_name TYPE VARCHAR(150),
    ALTER COLUMN objective TYPE VARCHAR(500);

ALTER TABLE detection.scenario
    ALTER COLUMN objective SET NOT NULL;

-- Add current repository columns.

ALTER TABLE detection.scenario
    ADD COLUMN criticality VARCHAR(30),
    ADD COLUMN owner VARCHAR(120),
    ADD COLUMN version INTEGER NOT NULL DEFAULT 1,
    ADD COLUMN correlation_window_minutes INTEGER,
    ADD COLUMN maximum_execution_time_seconds INTEGER,
    ADD COLUMN minimum_events INTEGER,
    ADD COLUMN minimum_confidence NUMERIC(8,4),
    ADD COLUMN minimum_evidence INTEGER,
    ADD COLUMN required_rules JSONB,
    ADD COLUMN required_variables JSONB,
    ADD COLUMN evidence_requirements JSONB,
    ADD COLUMN exclusions JSONB,
    ADD COLUMN exceptions JSONB,
    ADD COLUMN suggested_actions JSONB,
    ADD COLUMN related_scenarios JSONB,
    ADD COLUMN configuration_context JSONB;

-- Current scenario uniqueness model.

ALTER TABLE detection.scenario
    ADD CONSTRAINT uq_detection_scenario_code_version
        UNIQUE (scenario_code, version);

-- Current repository indexes.
-- idx_detection_scenario_code, idx_detection_scenario_category and
-- idx_detection_scenario_status already exist from V96 and are preserved.

CREATE INDEX idx_detection_scenario_name
    ON detection.scenario (scenario_name);

CREATE INDEX idx_detection_scenario_criticality
    ON detection.scenario (criticality);

CREATE INDEX idx_detection_scenario_owner
    ON detection.scenario (owner);

CREATE INDEX idx_detection_scenario_version
    ON detection.scenario (scenario_code, version);

CREATE INDEX idx_detection_scenario_min_confidence
    ON detection.scenario (minimum_confidence);

CREATE INDEX idx_detection_scenario_window
    ON detection.scenario (correlation_window_minutes);

CREATE INDEX gin_detection_scenario_required_rules
    ON detection.scenario USING GIN (required_rules);

CREATE INDEX gin_detection_scenario_required_variables
    ON detection.scenario USING GIN (required_variables);

CREATE INDEX gin_detection_scenario_evidence_requirements
    ON detection.scenario USING GIN (evidence_requirements);

CREATE INDEX gin_detection_scenario_exclusions
    ON detection.scenario USING GIN (exclusions);

CREATE INDEX gin_detection_scenario_exceptions
    ON detection.scenario USING GIN (exceptions);

CREATE INDEX gin_detection_scenario_actions
    ON detection.scenario USING GIN (suggested_actions);

CREATE INDEX gin_detection_scenario_related
    ON detection.scenario USING GIN (related_scenarios);

CREATE INDEX gin_detection_scenario_context
    ON detection.scenario USING GIN (configuration_context);