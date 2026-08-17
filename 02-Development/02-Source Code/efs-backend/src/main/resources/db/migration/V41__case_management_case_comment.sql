-- EFS-DB-002
-- V41 - Case Management Case Comment

CREATE TABLE case_management.case_comment (
    comment_id UUID NOT NULL DEFAULT uuidv7(),
    case_id UUID NOT NULL,
    comment_type VARCHAR(30) NOT NULL,
    comment_text TEXT NOT NULL,
    visibility VARCHAR(20) NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_case_comment
        PRIMARY KEY (comment_id),

    CONSTRAINT fk_case_comment_case
        FOREIGN KEY (case_id)
        REFERENCES case_management.case (case_id),

    CONSTRAINT fk_case_comment_created_by
        FOREIGN KEY (created_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_comment_case
    ON case_management.case_comment (case_id);

CREATE INDEX idx_comment_type
    ON case_management.case_comment (comment_type);

CREATE INDEX idx_comment_user
    ON case_management.case_comment (created_by);

CREATE INDEX idx_comment_date
    ON case_management.case_comment (created_at);