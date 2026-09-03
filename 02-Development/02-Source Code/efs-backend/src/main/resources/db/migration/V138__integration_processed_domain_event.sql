-- EFS-DB-002
-- V138 - Processed Domain Event

CREATE TABLE integration.processed_domain_event (
    message_id UUID NOT NULL,
    consumer_name VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_processed_domain_event
        PRIMARY KEY (
            message_id,
            consumer_name
        )
);