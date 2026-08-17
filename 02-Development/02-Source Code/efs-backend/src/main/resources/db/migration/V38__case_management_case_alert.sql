-- EFS-DB-002
-- V38 - Case Management Case Alert

CREATE TABLE case_management.case_alert (
    alert_id UUID NOT NULL DEFAULT uuidv7(),
    case_id UUID NOT NULL,
    transaction_id UUID,
    alert_type VARCHAR(40) NOT NULL,
    alert_source VARCHAR(50) NOT NULL,
    risk_score NUMERIC(8,2),
    severity VARCHAR(20) NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_case_alert
        PRIMARY KEY (alert_id),

    CONSTRAINT fk_case_alert_case
        FOREIGN KEY (case_id)
        REFERENCES case_management.case (case_id),

    CONSTRAINT fk_case_alert_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_alert_case
    ON case_management.case_alert (case_id);

CREATE INDEX idx_alert_source
    ON case_management.case_alert (alert_source);

CREATE INDEX idx_alert_severity
    ON case_management.case_alert (severity);

CREATE INDEX idx_alert_transaction
    ON case_management.case_alert (transaction_id);