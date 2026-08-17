-- EFS-DB-002
-- V47 - Case Management Case Notification
-- Controlled Physical Design Decision:
-- Case-scoped notification trace only.
-- Notification delivery remains responsibility of Notification Service.

CREATE TABLE case_management.case_notification (
    case_notification_id UUID NOT NULL DEFAULT uuidv7(),
    case_id UUID NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    recipient_user_id UUID,
    notification_status VARCHAR(30) NOT NULL,
    notification_reference VARCHAR(120),
    delivery_result VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,

    CONSTRAINT pk_case_notification
        PRIMARY KEY (case_notification_id),

    CONSTRAINT fk_case_notification_case
        FOREIGN KEY (case_id)
        REFERENCES case_management.case (case_id),

    CONSTRAINT fk_case_notification_recipient
        FOREIGN KEY (recipient_user_id)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_case_notification_case
    ON case_management.case_notification (case_id);

CREATE INDEX idx_case_notification_recipient
    ON case_management.case_notification (recipient_user_id);

CREATE INDEX idx_case_notification_status
    ON case_management.case_notification (notification_status);

CREATE INDEX idx_case_notification_created
    ON case_management.case_notification (created_at);