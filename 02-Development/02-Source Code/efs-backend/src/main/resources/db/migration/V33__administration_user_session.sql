-- EFS-DB-002
-- V33 - Administration User Session

CREATE TABLE administration.user_session (
    session_id UUID NOT NULL DEFAULT uuidv7(),
    user_id UUID NOT NULL,
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    logout_time TIMESTAMP,
    ip_address INET,
    user_agent TEXT,
    device_fingerprint VARCHAR(255),
    authentication_method VARCHAR(60),
    mfa_result VARCHAR(30),
    session_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_user_session
        PRIMARY KEY (session_id),

    CONSTRAINT fk_user_session_user
        FOREIGN KEY (user_id)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_session_user
    ON administration.user_session (user_id);

CREATE INDEX idx_session_login
    ON administration.user_session (login_time);

CREATE INDEX idx_session_status
    ON administration.user_session (session_status);

CREATE INDEX idx_session_ip
    ON administration.user_session (ip_address);