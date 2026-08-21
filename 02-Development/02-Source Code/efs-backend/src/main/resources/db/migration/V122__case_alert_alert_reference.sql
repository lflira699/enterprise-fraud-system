-- EFS-DB-002
-- V122 - Case Alert Reference to Alert Domain

ALTER TABLE case_management.case_alert
    ADD COLUMN source_alert_id UUID;

ALTER TABLE case_management.case_alert
    ADD CONSTRAINT fk_case_alert_source_alert
        FOREIGN KEY (source_alert_id)
        REFERENCES alert.alert (alert_id);

CREATE INDEX idx_case_alert_source_alert
    ON case_management.case_alert (source_alert_id);