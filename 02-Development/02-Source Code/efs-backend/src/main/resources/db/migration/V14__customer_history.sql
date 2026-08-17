-- EFS-DB-002
-- V14 - Customer History

CREATE TABLE customer.customer_history (
    customer_history_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_description VARCHAR(500),
    previous_status VARCHAR(30),
    new_status VARCHAR(30),
    previous_risk_level VARCHAR(20),
    new_risk_level VARCHAR(20),
    previous_risk_score NUMERIC(8,2),
    new_risk_score NUMERIC(8,2),
    event_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    source_reference VARCHAR(150),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,

    CONSTRAINT pk_customer_history
        PRIMARY KEY (customer_history_id),

    CONSTRAINT fk_customer_history_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id)
);

CREATE INDEX idx_customer_history_customer
    ON customer.customer_history (customer_id);

CREATE INDEX idx_customer_history_event_type
    ON customer.customer_history (event_type);

CREATE INDEX idx_customer_history_event_timestamp
    ON customer.customer_history (event_timestamp);

CREATE INDEX idx_customer_history_risk
    ON customer.customer_history (new_risk_level);