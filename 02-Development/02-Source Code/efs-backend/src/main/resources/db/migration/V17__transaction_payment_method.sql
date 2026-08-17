-- EFS-DB-002
-- V17 - Transaction Payment Method

CREATE TABLE transaction.transaction_payment_method (
    payment_method_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    payment_type VARCHAR(40) NOT NULL,
    network VARCHAR(40),
    issuer VARCHAR(120),
    masked_identifier VARCHAR(50),
    token_reference VARCHAR(200),
    expiration_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_payment_method
        PRIMARY KEY (payment_method_id),

    CONSTRAINT fk_transaction_payment_method_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id)
);

CREATE INDEX idx_payment_transaction
    ON transaction.transaction_payment_method (transaction_id);

CREATE INDEX idx_payment_type
    ON transaction.transaction_payment_method (payment_type);

CREATE INDEX idx_payment_network
    ON transaction.transaction_payment_method (network);

CREATE INDEX idx_payment_token
    ON transaction.transaction_payment_method (token_reference);