-- EFS-DB-002
-- V75 - Catalog Risk Level
-- Controlled Physical Design Decision:
-- Specialized catalog of risk levels used by analytical and fraud engines.

CREATE TABLE catalog.risk_level (
    risk_level_id UUID NOT NULL DEFAULT uuidv7(),
    risk_code VARCHAR(30) NOT NULL,
    risk_name VARCHAR(100) NOT NULL,
    description TEXT,
    display_order SMALLINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_risk_level
        PRIMARY KEY (risk_level_id),

    CONSTRAINT uk_risk_level_code
        UNIQUE (risk_code)
);

CREATE INDEX idx_risk_level_name
    ON catalog.risk_level (risk_name);

CREATE INDEX idx_risk_level_order
    ON catalog.risk_level (display_order);

CREATE INDEX idx_risk_level_status
    ON catalog.risk_level (status);