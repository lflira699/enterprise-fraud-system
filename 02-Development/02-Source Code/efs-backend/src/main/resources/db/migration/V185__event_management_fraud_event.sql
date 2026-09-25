CREATE SCHEMA IF NOT EXISTS event_management;

CREATE TABLE event_management.fraud_event (
    fraud_event_id UUID NOT NULL DEFAULT uuidv7(),
    organization_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    transaction_id UUID NULL,
    event_type VARCHAR(100) NOT NULL,
    source_type VARCHAR(60) NOT NULL,
    source_reference VARCHAR(255) NULL,
    idempotency_key VARCHAR(255) NOT NULL,
    correlation_id UUID NOT NULL,
    normalized_payload JSONB NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    received_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_fraud_event
        PRIMARY KEY (fraud_event_id),

    CONSTRAINT fk_fraud_event_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_fraud_event_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id),

    CONSTRAINT fk_fraud_event_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT uq_fraud_event_idempotency
        UNIQUE (organization_id, tenant_id, idempotency_key),

    CONSTRAINT ck_fraud_event_received_at
        CHECK (received_at >= occurred_at),

    CONSTRAINT ck_fraud_event_created_at
        CHECK (created_at >= received_at)
);

CREATE INDEX idx_fraud_event_organization_tenant
    ON event_management.fraud_event (organization_id, tenant_id);

CREATE INDEX idx_fraud_event_transaction
    ON event_management.fraud_event (transaction_id);

CREATE INDEX idx_fraud_event_event_type
    ON event_management.fraud_event (event_type);

CREATE INDEX idx_fraud_event_source_type
    ON event_management.fraud_event (source_type);

CREATE INDEX idx_fraud_event_source_reference
    ON event_management.fraud_event (source_reference);

CREATE INDEX idx_fraud_event_correlation
    ON event_management.fraud_event (correlation_id);

CREATE INDEX idx_fraud_event_occurred_at
    ON event_management.fraud_event (occurred_at);

CREATE INDEX idx_fraud_event_received_at
    ON event_management.fraud_event (received_at);

CREATE INDEX idx_fraud_event_created_at
    ON event_management.fraud_event (created_at);
