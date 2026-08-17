-- EFS-DB-002
-- V37 - Case Management Case

CREATE TABLE case_management.case (
    case_id UUID NOT NULL DEFAULT uuidv7(),
    case_number VARCHAR(50) NOT NULL,
    organization_id UUID NOT NULL,
    transaction_id UUID,
    customer_id UUID,
    case_type VARCHAR(40) NOT NULL,
    category VARCHAR(40) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    current_status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    assigned_team VARCHAR(100),
    assigned_user UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP,
    closed_at TIMESTAMP,
    tenant_id UUID,

    CONSTRAINT pk_case
        PRIMARY KEY (case_id),

    CONSTRAINT uk_case_number
        UNIQUE (case_number),

    CONSTRAINT fk_case_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_case_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_case_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer.customer (customer_id),

    CONSTRAINT fk_case_assigned_user
        FOREIGN KEY (assigned_user)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT fk_case_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id)
);

CREATE INDEX idx_case_number
    ON case_management.case (case_number);

CREATE INDEX idx_case_status
    ON case_management.case (current_status);

CREATE INDEX idx_case_priority
    ON case_management.case (priority);

CREATE INDEX idx_case_assigned_user
    ON case_management.case (assigned_user);

CREATE INDEX idx_case_customer
    ON case_management.case (customer_id);

CREATE INDEX idx_case_transaction
    ON case_management.case (transaction_id);

CREATE INDEX idx_case_created
    ON case_management.case (created_at);