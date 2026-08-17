-- EFS-DB-002
-- V74 - Catalog Timezone
-- Controlled Physical Design Decision:
-- Specialized catalog of canonical time zones supported by EFS.

CREATE TABLE catalog.timezone (
    timezone_id UUID NOT NULL DEFAULT uuidv7(),
    timezone_code VARCHAR(100) NOT NULL,
    timezone_name VARCHAR(150) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_timezone
        PRIMARY KEY (timezone_id),

    CONSTRAINT uk_timezone_code
        UNIQUE (timezone_code)
);

CREATE INDEX idx_timezone_name
    ON catalog.timezone (timezone_name);

CREATE INDEX idx_timezone_status
    ON catalog.timezone (status);