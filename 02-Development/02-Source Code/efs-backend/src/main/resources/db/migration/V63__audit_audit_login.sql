-- EFS-DB-002
-- V63 - Audit Login
-- Controlled Physical Design Decision:
-- Completes physical typing for the approved audit_login structure.

CREATE TABLE audit.audit_login (
    login_id UUID NOT NULL DEFAULT uuidv7(),
    user_id UUID,
    login_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address INET,
    device_fingerprint VARCHAR(255),
    authentication_method VARCHAR(60) NOT NULL,
    mfa_result VARCHAR(30),
    login_result VARCHAR(30) NOT NULL,
    failure_reason TEXT,
    country_code CHAR(2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_audit_login
        PRIMARY KEY (login_id),

    CONSTRAINT fk_audit_login_user
        FOREIGN KEY (user_id)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_login_user
    ON audit.audit_login (user_id);

CREATE INDEX idx_login_date
    ON audit.audit_login (login_timestamp);

CREATE INDEX idx_login_ip
    ON audit.audit_login (ip_address);

CREATE INDEX idx_login_result
    ON audit.audit_login (login_result);