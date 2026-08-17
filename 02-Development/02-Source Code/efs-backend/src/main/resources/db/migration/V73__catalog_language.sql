-- EFS-DB-002
-- V73 - Catalog Language
-- Controlled Physical Design Decision:
-- Specialized language catalog based on ISO 639.

CREATE TABLE catalog.language (
    language_id UUID NOT NULL DEFAULT uuidv7(),
    language_code CHAR(2) NOT NULL,
    alpha3_code CHAR(3) NOT NULL,
    language_name VARCHAR(150) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_language
        PRIMARY KEY (language_id),

    CONSTRAINT uk_language_code
        UNIQUE (language_code),

    CONSTRAINT uk_language_alpha3
        UNIQUE (alpha3_code)
);

CREATE INDEX idx_language_name
    ON catalog.language (language_name);

CREATE INDEX idx_language_status
    ON catalog.language (status);