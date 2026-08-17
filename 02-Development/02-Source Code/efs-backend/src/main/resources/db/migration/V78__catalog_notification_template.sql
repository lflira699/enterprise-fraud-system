-- EFS-DB-002
-- V78 - Catalog Notification Template
-- Controlled Physical Design Decision:
-- Reusable notification templates for channels supported by EFS.
-- Notification delivery remains responsibility of Notification Service.

CREATE TABLE catalog.notification_template (
    notification_template_id UUID NOT NULL DEFAULT uuidv7(),
    organization_id UUID,
    tenant_id UUID,
    language_id UUID,
    template_code VARCHAR(60) NOT NULL,
    template_name VARCHAR(150) NOT NULL,
    channel VARCHAR(30) NOT NULL,
    subject_template VARCHAR(250),
    body_template TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_notification_template
        PRIMARY KEY (notification_template_id),

    CONSTRAINT fk_notification_template_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_notification_template_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id),

    CONSTRAINT fk_notification_template_language
        FOREIGN KEY (language_id)
        REFERENCES catalog.language (language_id),

    CONSTRAINT uq_notification_template_scope
        UNIQUE (
            organization_id,
            tenant_id,
            template_code,
            channel,
            language_id
        )
);

CREATE INDEX idx_notification_template_code
    ON catalog.notification_template (template_code);

CREATE INDEX idx_notification_template_channel
    ON catalog.notification_template (channel);

CREATE INDEX idx_notification_template_status
    ON catalog.notification_template (status);

CREATE INDEX idx_notification_template_organization
    ON catalog.notification_template (organization_id);

CREATE INDEX idx_notification_template_tenant
    ON catalog.notification_template (tenant_id);

CREATE INDEX idx_notification_template_language
    ON catalog.notification_template (language_id);