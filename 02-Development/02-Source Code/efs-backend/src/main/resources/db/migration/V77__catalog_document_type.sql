-- EFS-DB-002
-- V77 - Catalog Document Type
-- Controlled Physical Design Decision:
-- Specialized catalog of identification document types accepted by organizations.

CREATE TABLE catalog.document_type (
    document_type_id UUID NOT NULL DEFAULT uuidv7(),
    organization_id UUID NOT NULL,
    document_type_code VARCHAR(60) NOT NULL,
    document_type_name VARCHAR(150) NOT NULL,
    description TEXT,
    display_order SMALLINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_document_type
        PRIMARY KEY (document_type_id),

    CONSTRAINT fk_document_type_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT uk_document_type_org_code
        UNIQUE (organization_id, document_type_code)
);

CREATE INDEX idx_document_type_organization
    ON catalog.document_type (organization_id);

CREATE INDEX idx_document_type_name
    ON catalog.document_type (document_type_name);

CREATE INDEX idx_document_type_order
    ON catalog.document_type (display_order);

CREATE INDEX idx_document_type_status
    ON catalog.document_type (status);