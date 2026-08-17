-- EFS-DB-002
-- V42 - Case Management Case Evidence

CREATE TABLE case_management.case_evidence (
    evidence_id UUID NOT NULL DEFAULT uuidv7(),
    case_id UUID NOT NULL,
    transaction_id UUID,
    evidence_type VARCHAR(60) NOT NULL,
    source_system VARCHAR(80) NOT NULL,
    storage_uri TEXT,
    checksum_sha256 VARCHAR(64),
    uploaded_by UUID,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_case_evidence
        PRIMARY KEY (evidence_id),

    CONSTRAINT fk_case_evidence_case
        FOREIGN KEY (case_id)
        REFERENCES case_management.case (case_id),

    CONSTRAINT fk_case_evidence_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transaction.transaction (transaction_id),

    CONSTRAINT fk_case_evidence_uploaded_by
        FOREIGN KEY (uploaded_by)
        REFERENCES administration.user_account (user_id)
);

CREATE INDEX idx_evidence_case
    ON case_management.case_evidence (case_id);

CREATE INDEX idx_evidence_transaction
    ON case_management.case_evidence (transaction_id);

CREATE INDEX idx_evidence_type
    ON case_management.case_evidence (evidence_type);