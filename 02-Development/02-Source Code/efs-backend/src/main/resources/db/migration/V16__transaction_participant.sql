-- EFS-DB-002
-- V16 - Transaction Participant

CREATE TABLE transaction.transaction_participant (
    participant_id UUID NOT NULL DEFAULT uuidv7(),
    transaction_id UUID NOT NULL,
    participant_type VARCHAR(40) NOT NULL,
    customer_id UUID,
    external_identifier VARCHAR(150),
    institution_id UUID,
    country_code CHAR(2),
    risk_level VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transaction_participant
        PRIMARY KEY (participant_id),

    CONSTRAINT fk_transaction_participant_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_transaction_participant_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id)
);

CREATE INDEX idx_participant_transaction
    ON transaction.transaction_participant (transaction_id);

CREATE INDEX idx_participant_customer
    ON transaction.transaction_participant (customer_id);

CREATE INDEX idx_participant_role
    ON transaction.transaction_participant (participant_type);

CREATE INDEX idx_participant_country
    ON transaction.transaction_participant (country_code);