-- EFS-DB-002
-- V124 - Alert Identity and Context Alignment

ALTER TABLE alert.alert
    ADD COLUMN alert_reference VARCHAR(60),
    ADD COLUMN customer_id UUID,
    ADD COLUMN severity VARCHAR(20),
    ADD COLUMN title VARCHAR(200),
    ADD COLUMN description TEXT,
    ADD COLUMN risk_assessment_id UUID,
    ADD COLUMN scenario_id UUID,
    ADD COLUMN rule_id UUID;

ALTER TABLE alert.alert
    ADD CONSTRAINT uk_alert_reference
        UNIQUE (alert_reference);

ALTER TABLE alert.alert
    ADD CONSTRAINT fk_alert_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id);

ALTER TABLE alert.alert
    ADD CONSTRAINT fk_alert_risk_assessment
        FOREIGN KEY (risk_assessment_id)
        REFERENCES transaction.risk_assessment (risk_assessment_id);

ALTER TABLE alert.alert
    ADD CONSTRAINT fk_alert_scenario
        FOREIGN KEY (scenario_id)
        REFERENCES detection.scenario (scenario_id);

ALTER TABLE alert.alert
    ADD CONSTRAINT fk_alert_rule
        FOREIGN KEY (rule_id)
        REFERENCES rules.rule (rule_id);

CREATE INDEX idx_alert_customer
    ON alert.alert (customer_id);

CREATE INDEX idx_alert_risk_assessment
    ON alert.alert (risk_assessment_id);

CREATE INDEX idx_alert_scenario
    ON alert.alert (scenario_id);

CREATE INDEX idx_alert_rule
    ON alert.alert (rule_id);

CREATE INDEX idx_alert_severity
    ON alert.alert (severity);