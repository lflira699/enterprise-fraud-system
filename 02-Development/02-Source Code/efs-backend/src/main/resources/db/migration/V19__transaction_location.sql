-- EFS-DB-002
-- V19 - Transaction Location

CREATE TABLE transaction.transaction_location (
    location_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    ip_address INET,
    country_code CHAR(2),
    state VARCHAR(120),
    city VARCHAR(120),
    postal_code VARCHAR(30),
    latitude NUMERIC(10,7),
    longitude NUMERIC(10,7),
    asn BIGINT,
    internet_provider VARCHAR(150),
    vpn_detected BOOLEAN NOT NULL DEFAULT FALSE,
    proxy_detected BOOLEAN NOT NULL DEFAULT FALSE,
    tor_detected BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_location
        PRIMARY KEY (location_id),

    CONSTRAINT fk_transaction_location_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_location_transaction
    ON transaction.transaction_location (transaction_id);

CREATE INDEX idx_location_ip
    ON transaction.transaction_location (ip_address);

CREATE INDEX idx_location_country
    ON transaction.transaction_location (country_code);

CREATE INDEX idx_location_asn
    ON transaction.transaction_location (asn);