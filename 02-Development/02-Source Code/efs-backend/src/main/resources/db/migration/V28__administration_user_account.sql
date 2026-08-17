-- EFS-DB-002
-- V28 - Administration User Account

CREATE TABLE administration.user_account (
    user_id UUID NOT NULL DEFAULT uuidv7(),
    organization_id UUID NOT NULL,
    tenant_id UUID,
    business_unit_id UUID,
    username VARCHAR(100) NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL,
    authentication_provider VARCHAR(60) NOT NULL,
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    account_status VARCHAR(20) NOT NULL,
    last_login TIMESTAMP,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_user_account
        PRIMARY KEY (user_id),

    CONSTRAINT fk_user_account_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_user_account_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id),

    CONSTRAINT fk_user_account_business_unit
        FOREIGN KEY (business_unit_id)
        REFERENCES administration.business_unit (business_unit_id)
);

CREATE INDEX idx_user_username
    ON administration.user_account (username);

CREATE INDEX idx_user_email
    ON administration.user_account (email);

CREATE INDEX idx_user_status
    ON administration.user_account (account_status);

CREATE INDEX idx_user_organization
    ON administration.user_account (organization_id);