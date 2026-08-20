-- EFS-DB-002
-- V112 - Detection Scenario Evaluation Alignment

-- Align detection.scenario_evaluation created by V101
-- with the current ScenarioEvaluation repository model.

ALTER TABLE detection.scenario_evaluation
    ALTER COLUMN rule_count SET NOT NULL,
    ALTER COLUMN matched_rule_count SET NOT NULL,
    ALTER COLUMN required_evidence_count SET NOT NULL,
    ALTER COLUMN available_evidence_count SET NOT NULL;

ALTER TABLE detection.scenario_evaluation
    ALTER COLUMN risk_contribution TYPE NUMERIC(8,4);

ALTER TABLE detection.scenario_evaluation
    ALTER COLUMN matched SET DEFAULT FALSE;

-- Existing indexes created by V101 are preserved.