-- EFS-DB-002
-- V120 - Alert

CREATE TABLE alert.alert (
    alert_id UUID NOT NULL DEFAULT uuidv7(),

    transaction_id UUID,
    decision_id UUID NOT NULL,

    alert_type VARCHAR(40) NOT NULL,
    category VARCHAR(40),

    priority VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',

    risk_score NUMERIC(8,2),
    correlation_id UUID,

    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_alert
        PRIMARY KEY (alert_id),

    CONSTRAINT fk_alert_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_alert_decision
        FOREIGN KEY (decision_id)
        REFERENCES transaction.transaction_decision (decision_id)
);

CREATE INDEX idx_alert_transaction
    ON alert.alert (transaction_id);

CREATE INDEX idx_alert_decision
    ON alert.alert (decision_id);

CREATE INDEX idx_alert_status
    ON alert.alert (status);

CREATE INDEX idx_alert_priority
    ON alert.alert (priority);

CREATE INDEX idx_alert_generated_at
    ON alert.alert (generated_at);