CREATE TABLE integration.integration_subscription (
    subscription_id UUID NOT NULL DEFAULT uuidv7(),
    event_id UUID NOT NULL,
    subscriber VARCHAR(150) NOT NULL,
    delivery_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_integration_subscription
        PRIMARY KEY (subscription_id),

    CONSTRAINT fk_integration_subscription_event
        FOREIGN KEY (event_id)
        REFERENCES integration.integration_event (event_id)
);

CREATE INDEX idx_subscription_event
    ON integration.integration_subscription (event_id);

CREATE INDEX idx_subscription_status
    ON integration.integration_subscription (status);