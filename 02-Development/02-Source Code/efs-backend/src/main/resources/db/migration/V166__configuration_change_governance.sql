-- EFS UC-044
-- Configuration Governance Contract v1
-- Current published configuration remains administration.system_configuration.
-- These structures govern validation, approval, version history and publication.

CREATE TABLE administration.configuration_change_request (
    change_request_id UUID NOT NULL DEFAULT uuidv7(),
    organization_id UUID NOT NULL,
    tenant_id UUID,
    requested_by UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    justification TEXT NOT NULL,
    affected_environment VARCHAR(50) NOT NULL,
    risk_assessment TEXT NOT NULL,
    expected_result TEXT NOT NULL,
    rollback_plan TEXT NOT NULL,
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_by UUID,
    approved_at TIMESTAMP,
    rejected_by UUID,
    rejected_at TIMESTAMP,
    rejection_reason TEXT,
    applied_by UUID,
    applied_at TIMESTAMP,
    failure_reason TEXT,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_configuration_change_request
        PRIMARY KEY (change_request_id),

    CONSTRAINT fk_configuration_change_request_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_configuration_change_request_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id),

    CONSTRAINT fk_configuration_change_request_requested_by
        FOREIGN KEY (requested_by)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT fk_configuration_change_request_approved_by
        FOREIGN KEY (approved_by)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT fk_configuration_change_request_rejected_by
        FOREIGN KEY (rejected_by)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT fk_configuration_change_request_applied_by
        FOREIGN KEY (applied_by)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT ck_configuration_change_request_status
        CHECK (
            status IN (
                'PENDING_APPROVAL',
                'APPROVED',
                'APPLIED',
                'REJECTED',
                'FAILED'
            )
        )
);

CREATE INDEX idx_configuration_change_request_scope
    ON administration.configuration_change_request (
        organization_id,
        tenant_id,
        requested_at DESC
    );

CREATE INDEX idx_configuration_change_request_status
    ON administration.configuration_change_request (
        status,
        requested_at DESC
    );

CREATE INDEX idx_configuration_change_request_requested_by
    ON administration.configuration_change_request (
        requested_by
    );

CREATE TABLE administration.configuration_change_item (
    change_item_id UUID NOT NULL DEFAULT uuidv7(),
    change_request_id UUID NOT NULL,
    configuration_key VARCHAR(150) NOT NULL,
    previous_value TEXT,
    proposed_value TEXT NOT NULL,
    configuration_type VARCHAR(50) NOT NULL,
    encrypted BOOLEAN NOT NULL DEFAULT FALSE,
    version_number INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_configuration_change_item
        PRIMARY KEY (change_item_id),

    CONSTRAINT fk_configuration_change_item_request
        FOREIGN KEY (change_request_id)
        REFERENCES administration.configuration_change_request (
            change_request_id
        ),

    CONSTRAINT uq_configuration_change_item_request_key
        UNIQUE (
            change_request_id,
            configuration_key
        ),

    CONSTRAINT ck_configuration_change_item_version
        CHECK (
            version_number IS NULL
            OR version_number > 0
        )
);

CREATE INDEX idx_configuration_change_item_request
    ON administration.configuration_change_item (
        change_request_id
    );

CREATE INDEX idx_configuration_change_item_key
    ON administration.configuration_change_item (
        configuration_key
    );

CREATE INDEX idx_configuration_change_item_key_version
    ON administration.configuration_change_item (
        configuration_key,
        version_number DESC
    );