-- EFS-DB-002 v2.1
-- EFS-TC-001 - Notification Delivery Technical Contract v1.0
-- V152 - Notification Aggregate Root
-- UC-038 - Notification Delivery
-- COMP-013 - Notification Service

CREATE TABLE notification.notification (
    notification_id UUID NOT NULL DEFAULT uuidv7(),
    organization_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    notification_template_id UUID,
    source_component VARCHAR(100) NOT NULL,
    source_entity_type VARCHAR(60) NOT NULL,
    source_entity_id UUID NOT NULL,
    correlation_id UUID NOT NULL,
    notification_payload JSONB NOT NULL,
    notification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,

    CONSTRAINT pk_notification
        PRIMARY KEY (notification_id),

    CONSTRAINT fk_notification_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_notification_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id),

    CONSTRAINT fk_notification_template
        FOREIGN KEY (notification_template_id)
        REFERENCES catalog.notification_template (notification_template_id),

    CONSTRAINT ck_notification_status
        CHECK (
            notification_status IN (
                'PENDING',
                'PROCESSING',
                'DELIVERED',
                'PARTIALLY_DELIVERED',
                'FAILED'
            )
        ),

    CONSTRAINT ck_notification_processed_at
        CHECK (
            processed_at IS NULL
            OR processed_at >= created_at
        ),

    CONSTRAINT ck_notification_terminal_processed_at
        CHECK (
            notification_status NOT IN (
                'DELIVERED',
                'PARTIALLY_DELIVERED',
                'FAILED'
            )
            OR processed_at IS NOT NULL
        )
);

CREATE INDEX idx_notification_scope
    ON notification.notification (
        organization_id,
        tenant_id
    );

CREATE INDEX idx_notification_template
    ON notification.notification (
        notification_template_id
    );

CREATE INDEX idx_notification_status
    ON notification.notification (
        notification_status
    );

CREATE INDEX idx_notification_correlation
    ON notification.notification (
        correlation_id
    );

CREATE INDEX idx_notification_source
    ON notification.notification (
        source_component,
        source_entity_type,
        source_entity_id
    );

CREATE INDEX idx_notification_created
    ON notification.notification (
        created_at
    );
