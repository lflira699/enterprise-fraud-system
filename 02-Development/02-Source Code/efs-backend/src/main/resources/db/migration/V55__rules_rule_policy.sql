-- EFS-DB-002
-- V55 - Rule Engine Rule Policy

CREATE TABLE rules.rule_policy (
    policy_id UUID NOT NULL DEFAULT uuidv7(),
    policy_code VARCHAR(60) NOT NULL,
    policy_name VARCHAR(180) NOT NULL,
    description TEXT,
    policy_type VARCHAR(40) NOT NULL,
    organization_id UUID,
    tenant_id UUID,
    status VARCHAR(20) NOT NULL,
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    priority SMALLINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_rule_policy
        PRIMARY KEY (policy_id),

    CONSTRAINT uk_rule_policy_code
        UNIQUE (policy_code),

    CONSTRAINT fk_rule_policy_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_rule_policy_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id)
);

CREATE INDEX idx_policy_code
    ON rules.rule_policy (policy_code);

CREATE INDEX idx_policy_status
    ON rules.rule_policy (status);

CREATE INDEX idx_policy_organization
    ON rules.rule_policy (organization_id);

CREATE INDEX idx_policy_effective
    ON rules.rule_policy (effective_from, effective_to);

CREATE INDEX idx_policy_priority
    ON rules.rule_policy (priority);