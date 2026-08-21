-- EFS-DB-002
-- V125 - Alert Operational Management

ALTER TABLE alert.alert
    ADD COLUMN assigned_to UUID,
    ADD COLUMN assigned_team VARCHAR(100),
    ADD COLUMN due_at TIMESTAMP;

ALTER TABLE alert.alert
    ADD CONSTRAINT fk_alert_assigned_to
        FOREIGN KEY (assigned_to)
        REFERENCES administration.user_account (user_id);

CREATE INDEX idx_alert_assigned_to
    ON alert.alert (assigned_to);

CREATE INDEX idx_alert_assigned_team
    ON alert.alert (assigned_team);

CREATE INDEX idx_alert_due_at
    ON alert.alert (due_at);