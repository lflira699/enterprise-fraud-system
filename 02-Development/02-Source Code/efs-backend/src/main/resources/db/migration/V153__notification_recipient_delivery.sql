-- EFS-DB-002 v2.1
-- EFS-TC-001 - Notification Delivery Technical Contract v1.0
-- V153 - Notification Recipient Delivery
-- UC-038 - Notification Delivery
-- COMP-013 - Notification Service

CREATE TABLE notification.notification_recipient_delivery (
    notification_delivery_id UUID NOT NULL DEFAULT uuidv7(),
    notification_id UUID NOT NULL,
    recipient_user_id UUID NOT NULL,
    channel VARCHAR(30) NOT NULL,
    delivery_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    delivery_reference VARCHAR(120),
    delivery_result VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,

    CONSTRAINT pk_notification_recipient_delivery
        PRIMARY KEY (notification_delivery_id),

    CONSTRAINT fk_notification_recipient_delivery_notification
        FOREIGN KEY (notification_id)
        REFERENCES notification.notification (notification_id),

    CONSTRAINT fk_notification_recipient_delivery_user
        FOREIGN KEY (recipient_user_id)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT ck_notification_recipient_delivery_status
        CHECK (
            delivery_status IN (
                'PENDING',
                'DELIVERED',
                'FAILED'
            )
        ),

    CONSTRAINT ck_notification_recipient_delivery_processed_at
        CHECK (
            processed_at IS NULL
            OR processed_at >= created_at
        ),

    CONSTRAINT ck_notification_recipient_delivery_terminal_processed_at
        CHECK (
            delivery_status NOT IN (
                'DELIVERED',
                'FAILED'
            )
            OR processed_at IS NOT NULL
        )
);

CREATE INDEX idx_notification_delivery_notification_status
    ON notification.notification_recipient_delivery (
        notification_id,
        delivery_status
    );

CREATE INDEX idx_notification_delivery_recipient
    ON notification.notification_recipient_delivery (
        recipient_user_id
    );

CREATE INDEX idx_notification_delivery_channel
    ON notification.notification_recipient_delivery (
        channel
    );
