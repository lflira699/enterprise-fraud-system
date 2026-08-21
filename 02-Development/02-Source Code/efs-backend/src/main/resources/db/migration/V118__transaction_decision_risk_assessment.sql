ALTER TABLE transaction.transaction_decision
    ADD COLUMN risk_assessment_id UUID;

ALTER TABLE transaction.transaction_decision
    ADD CONSTRAINT fk_transaction_decision_risk_assessment
        FOREIGN KEY (risk_assessment_id)
        REFERENCES transaction.risk_assessment (risk_assessment_id);

CREATE INDEX idx_transaction_decision_risk_assessment
    ON transaction.transaction_decision (risk_assessment_id);