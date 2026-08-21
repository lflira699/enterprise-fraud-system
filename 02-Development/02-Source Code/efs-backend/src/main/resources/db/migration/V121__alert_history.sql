-- EFS-DB-002
-- V121 - Alert History

CREATE TABLE alert.alert_history (
    alert_history_id UUID NOT NULL DEFAULT uuidv7(),

    alert_id UUID NOT NULL,

    action_type VARCHAR(40) NOT NULL,

    previous_status VARCHAR(30),
    new_status VARCHAR(30),

    changed_by UUID,
    change_reason TEXT,

    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_alert_history
        PRIMARY KEY (alert_history_id),

    CONSTRAINT fk_alert_history_alert
        FOREIGN KEY (alert_id)
        REFERENCES alert.alert (alert_id)
);

CREATE INDEX idx_alert_history_alert
    ON alert.alert_history (alert_id);

CREATE INDEX idx_alert_history_action
    ON alert.alert_history (action_type);

CREATE INDEX idx_alert_history_changed_at
    ON alert.alert_history (changed_at);