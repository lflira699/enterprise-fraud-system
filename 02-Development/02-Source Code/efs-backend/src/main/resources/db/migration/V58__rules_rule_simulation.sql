-- EFS-DB-002
-- V58 - Rule Engine Rule Simulation

CREATE TABLE rules.rule_simulation (
    simulation_id UUID NOT NULL DEFAULT uuidv7(),
    simulation_name VARCHAR(180) NOT NULL,
    entity_type VARCHAR(30) NOT NULL,
    entity_id UUID NOT NULL,
    dataset_reference VARCHAR(250) NOT NULL,
    sample_size BIGINT NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    simulation_status VARCHAR(30) NOT NULL,
    match_count BIGINT NOT NULL,
    approve_count BIGINT NOT NULL,
    reject_count BIGINT NOT NULL,
    review_count BIGINT NOT NULL,
    result_summary JSONB,
    executed_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_rule_simulation
        PRIMARY KEY (simulation_id),

    CONSTRAINT fk_rule_simulation_executed_by
        FOREIGN KEY (executed_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_simulation_entity
    ON rules.rule_simulation (entity_type, entity_id);

CREATE INDEX idx_simulation_status
    ON rules.rule_simulation (simulation_status);

CREATE INDEX idx_simulation_date
    ON rules.rule_simulation (started_at);

CREATE INDEX idx_simulation_executed_by
    ON rules.rule_simulation (executed_by);