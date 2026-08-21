-- EFS-DB-002
-- V127 - Alert Priority Score

ALTER TABLE alert.alert
    ADD COLUMN priority_score INTEGER;

ALTER TABLE alert.alert
    ADD CONSTRAINT ck_alert_priority_score
        CHECK (
            priority_score IS NULL
            OR priority_score BETWEEN 1 AND 100
        );

CREATE INDEX idx_alert_priority_score
    ON alert.alert (priority_score);