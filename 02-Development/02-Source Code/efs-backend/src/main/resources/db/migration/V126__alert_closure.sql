-- EFS-DB-002
-- V126 - Alert Closure

ALTER TABLE alert.alert
    ADD COLUMN closed_at TIMESTAMP,
    ADD COLUMN closure_reason TEXT;

CREATE INDEX idx_alert_closed_at
    ON alert.alert (closed_at);