-- EFS-DB-002
-- V60 - Rule Engine Rule History

CREATE TABLE rules.rule_history (
    history_id UUID NOT NULL DEFAULT uuidv7(),
    entity_type VARCHAR(30) NOT NULL,
    entity_id UUID NOT NULL,
    operation_type VARCHAR(30) NOT NULL,
    previous_value JSONB,
    current_value JSONB,
    change_reason TEXT,
    changed_by UUID NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    correlation_id UUID,

    CONSTRAINT pk_rule_history
        PRIMARY KEY (history_id),

    CONSTRAINT fk_rule_history_changed_by
        FOREIGN KEY (changed_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_history_entity
    ON rules.rule_history (entity_type, entity_id);

CREATE INDEX idx_history_date
    ON rules.rule_history (changed_at);

CREATE INDEX idx_history_user
    ON rules.rule_history (changed_by);

CREATE INDEX gin_history_previous
    ON rules.rule_history USING GIN (previous_value);

CREATE INDEX gin_history_current
    ON rules.rule_history USING GIN (current_value);