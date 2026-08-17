-- EFS-DB-002
-- V31 - Administration Role Permission

CREATE TABLE administration.role_permission (
    role_permission_id UUID NOT NULL DEFAULT uuidv7(),
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    granted_by UUID,

    CONSTRAINT pk_role_permission
        PRIMARY KEY (role_permission_id),

    CONSTRAINT fk_role_permission_role
        FOREIGN KEY (role_id)
        REFERENCES administration.role (role_id),

    CONSTRAINT fk_role_permission_permission
        FOREIGN KEY (permission_id)
        REFERENCES administration.permission (permission_id),

    CONSTRAINT fk_role_permission_granted_by
        FOREIGN KEY (granted_by)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT uq_role_permission_role_permission
        UNIQUE (role_id, permission_id)
);

CREATE INDEX idx_role_permission_role
    ON administration.role_permission (role_id);

CREATE INDEX idx_role_permission_permission
    ON administration.role_permission (permission_id);