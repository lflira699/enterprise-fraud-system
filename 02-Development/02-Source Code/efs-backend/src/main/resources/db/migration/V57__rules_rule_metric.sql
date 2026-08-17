-- EFS-DB-002
-- V57 - Rule Engine Rule Metric

CREATE TABLE rules.rule_metric (
    metric_id UUID NOT NULL DEFAULT uuidv7(),
    rule_id UUID NOT NULL,
    rule_version_id UUID,
    metric_date DATE NOT NULL,
    execution_count BIGINT NOT NULL,
    match_count BIGINT NOT NULL,
    confirmed_fraud_count BIGINT NOT NULL,
    false_positive_count BIGINT NOT NULL,
    false_negative_count BIGINT,
    average_execution_ms NUMERIC(12,2) NOT NULL,
    prevented_amount NUMERIC(20,2) NOT NULL,
    currency_code CHAR(3),
    calculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_rule_metric
        PRIMARY KEY (metric_id),

    CONSTRAINT fk_rule_metric_rule
        FOREIGN KEY (rule_id)
        REFERENCES rules.rule (rule_id),

    CONSTRAINT fk_rule_metric_rule_version
        FOREIGN KEY (rule_version_id)
        REFERENCES rules.rule_version (rule_version_id)
);

CREATE INDEX idx_metric_rule
    ON rules.rule_metric (rule_id);

CREATE INDEX idx_metric_version
    ON rules.rule_metric (rule_version_id);

CREATE INDEX idx_metric_date
    ON rules.rule_metric (metric_date);