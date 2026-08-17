-- EFS-DB-002
-- V13 - Customer Watchlist

CREATE TABLE customer.customer_watchlist (
    watchlist_id UUID NOT NULL DEFAULT uuidv7(),
    customer_id UUID NOT NULL,
    watchlist_type VARCHAR(50) NOT NULL,
    watchlist_source VARCHAR(120) NOT NULL,
    match_status VARCHAR(30) NOT NULL,
    match_score NUMERIC(8,2),
    matched_name VARCHAR(250),
    reference_id VARCHAR(150),
    detected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_checked_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP,
    record_version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT pk_customer_watchlist
        PRIMARY KEY (watchlist_id),

    CONSTRAINT fk_customer_watchlist_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id)
);

CREATE INDEX idx_customer_watchlist_customer
    ON customer.customer_watchlist (customer_id);

CREATE INDEX idx_customer_watchlist_type
    ON customer.customer_watchlist (watchlist_type);

CREATE INDEX idx_customer_watchlist_status
    ON customer.customer_watchlist (match_status);

CREATE INDEX idx_customer_watchlist_source
    ON customer.customer_watchlist (watchlist_source);

CREATE INDEX idx_customer_watchlist_detected
    ON customer.customer_watchlist (detected_at);