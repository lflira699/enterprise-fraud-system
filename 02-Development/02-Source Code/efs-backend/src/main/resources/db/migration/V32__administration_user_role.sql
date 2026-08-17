-- EFS-DB-002
-- V32 - Administration User Role

CREATE TABLE administration.user_role (
    user_role_id UUID NOT NULL DEFAULT uuidv7(),
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    effective_from TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    effective_to TIMESTAMP,
    assigned_by UUID,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_user_role
        PRIMARY KEY (user_role_id),

    CONSTRAINT fk_user_role_user
        FOREIGN KEY (user_id)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT fk_user_role_role
        FOREIGN KEY (role_id)
        REFERENCES administration.role (role_id),

    CONSTRAINT fk_user_role_assigned_by
        FOREIGN KEY (assigned_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_user_role_user
    ON administration.user_role (user_id);

CREATE INDEX idx_user_role_role
    ON administration.user_role (role_id);

CREATE INDEX idx_user_role_effective
    ON administration.user_role (effective_from, effective_to);