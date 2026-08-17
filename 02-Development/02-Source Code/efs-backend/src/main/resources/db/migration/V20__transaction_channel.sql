-- EFS-DB-002
-- V20 - Transaction Channel

CREATE TABLE transaction.transaction_channel (
    channel_transaction_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    channel_type VARCHAR(40) NOT NULL,
    application_name VARCHAR(120),
    application_version VARCHAR(50),
    sdk_version VARCHAR(50),
    api_version VARCHAR(50),
    authentication_method VARCHAR(60),
    session_duration INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_channel
        PRIMARY KEY (channel_transaction_id),

    CONSTRAINT fk_transaction_channel_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_channel_transaction
    ON transaction.transaction_channel (transaction_id);

CREATE INDEX idx_channel_type
    ON transaction.transaction_channel (channel_type);

CREATE INDEX idx_channel_application
    ON transaction.transaction_channel (application_name);