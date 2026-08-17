-- EFS-DB-002
-- V30 - Administration Permission

CREATE TABLE administration.permission (
    permission_id UUID NOT NULL DEFAULT uuidv7(),
    permission_code VARCHAR(80) NOT NULL,
    permission_name VARCHAR(150) NOT NULL,
    resource VARCHAR(80) NOT NULL,
    action VARCHAR(60) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_permission
        PRIMARY KEY (permission_id)
);

CREATE INDEX idx_permission_code
    ON administration.permission (permission_code);

CREATE INDEX idx_permission_resource
    ON administration.permission (resource);