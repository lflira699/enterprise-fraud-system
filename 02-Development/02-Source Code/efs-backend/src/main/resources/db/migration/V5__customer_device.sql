-- EFS-DB-002
-- V5 - Customer Device

CREATE TABLE customer.customer_device (
    device_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    device_fingerprint VARCHAR(255) NOT NULL,
    device_type VARCHAR(50),
    operating_system VARCHAR(100),
    browser VARCHAR(100),
    ip_address INET,
    country VARCHAR(100),
    city VARCHAR(100),
    trust_level VARCHAR(30),
    last_seen TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_customer_device
        PRIMARY KEY (device_id),

    CONSTRAINT fk_customer_device_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id)
);

CREATE INDEX idx_device_customer
    ON customer.customer_device (customer_id);

CREATE INDEX idx_device_fingerprint
    ON customer.customer_device (device_fingerprint);

CREATE INDEX idx_device_last_seen
    ON customer.customer_device (last_seen);