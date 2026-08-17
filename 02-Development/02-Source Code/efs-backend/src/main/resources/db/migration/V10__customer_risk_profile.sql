-- EFS-DB-002
-- V10 - Customer Risk Profile

CREATE TABLE customer.customer_risk_profile (
    profile_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    current_risk_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    risk_level VARCHAR(20) NOT NULL,
    last_calculation TIMESTAMP,
    behavior_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    fraud_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    aml_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    kyc_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    device_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    sanctions_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    pep_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    watchlist_score NUMERIC(8,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP,
    record_version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT pk_customer_risk_profile
        PRIMARY KEY (profile_id),

    CONSTRAINT fk_customer_risk_profile_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT uk_customer_risk_profile_customer
        UNIQUE (customer_id)
);

CREATE INDEX idx_customer_risk_profile_risk
    ON customer.customer_risk_profile (risk_level);

CREATE INDEX idx_customer_risk_profile_score
    ON customer.customer_risk_profile (current_risk_score);

CREATE INDEX idx_customer_risk_profile_calculation
    ON customer.customer_risk_profile (last_calculation);