-- EFS-DB-002
-- V18 - Transaction Device

CREATE TABLE transaction.transaction_device (
    device_transaction_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    device_id UUID,
    device_fingerprint VARCHAR(255),
    device_type VARCHAR(50),
    operating_system VARCHAR(100),
    os_version VARCHAR(50),
    browser VARCHAR(100),
    browser_version VARCHAR(50),
    screen_resolution VARCHAR(30),
    language VARCHAR(20),
    timezone VARCHAR(60),
    trust_score NUMERIC(8,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_device
        PRIMARY KEY (device_transaction_id),

    CONSTRAINT fk_transaction_device_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_device_transaction
    ON transaction.transaction_device (transaction_id);

CREATE INDEX idx_device_fingerprint
    ON transaction.transaction_device (device_fingerprint);

CREATE INDEX idx_device_trust
    ON transaction.transaction_device (trust_score);