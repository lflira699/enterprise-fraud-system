-- EFS-DB-002
-- V36 - Administration Feature Flag

CREATE TABLE administration.feature_flag (
    feature_flag_id UUID NOT NULL DEFAULT uuidv7(),
    feature_code VARCHAR(80) NOT NULL,
    feature_name VARCHAR(150) NOT NULL,
    description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    organization_id UUID,
    tenant_id UUID,
    activation_date TIMESTAMP,
    expiration_date TIMESTAMP,
    created_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_feature_flag
        PRIMARY KEY (feature_flag_id),

    CONSTRAINT fk_feature_flag_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_feature_flag_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id),

    CONSTRAINT fk_feature_flag_created_by
        FOREIGN KEY (created_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_feature_code
    ON administration.feature_flag (feature_code);

CREATE INDEX idx_feature_status
    ON administration.feature_flag (enabled);

CREATE INDEX idx_feature_organization
    ON administration.feature_flag (organization_id);

CREATE INDEX idx_feature_tenant
    ON administration.feature_flag (tenant_id);