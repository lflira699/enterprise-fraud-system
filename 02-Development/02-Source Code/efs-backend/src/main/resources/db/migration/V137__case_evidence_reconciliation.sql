-- EFS-DB-002
-- V137 - Case Evidence Reconciliation

ALTER TABLE case_management.case_evidence
    ADD COLUMN evidence_reference VARCHAR(60),
    ADD COLUMN evidence_category VARCHAR(50),
    ADD COLUMN evidence_name VARCHAR(200),
    ADD COLUMN evidence_description TEXT,
    ADD COLUMN file_name VARCHAR(255),
    ADD COLUMN file_format VARCHAR(60),
    ADD COLUMN file_size BIGINT,
    ADD COLUMN hash_algorithm VARCHAR(60),
    ADD COLUMN hash_value VARCHAR(512),
    ADD COLUMN chain_of_custody_reference VARCHAR(60),
    ADD COLUMN collected_by UUID,
    ADD COLUMN collected_at TIMESTAMP,
    ADD COLUMN validation_status VARCHAR(30),
    ADD COLUMN confidentiality_level VARCHAR(20),
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_by UUID,
    ADD COLUMN deleted_at TIMESTAMP,
    ADD COLUMN deleted_by UUID,
    ADD COLUMN record_version INTEGER NOT NULL DEFAULT 1;

UPDATE case_management.case_evidence
SET
    hash_algorithm = 'SHA-256',
    hash_value = checksum_sha256
WHERE checksum_sha256 IS NOT NULL;

UPDATE case_management.case_evidence
SET
    created_at = uploaded_at,
    created_by = uploaded_by,
    updated_at = uploaded_at;

ALTER TABLE case_management.case_evidence
    ADD CONSTRAINT fk_case_evidence_collected_by
        FOREIGN KEY (collected_by)
        REFERENCES administration.user_account (user_id);

ALTER TABLE case_management.case_evidence
    ADD CONSTRAINT uk_case_evidence_reference
        UNIQUE (case_id, evidence_reference);

CREATE INDEX idx_evidence_reference
    ON case_management.case_evidence (evidence_reference);

CREATE INDEX idx_evidence_category
    ON case_management.case_evidence (evidence_category);

CREATE INDEX idx_evidence_collected
    ON case_management.case_evidence (collected_at);

CREATE INDEX idx_evidence_hash
    ON case_management.case_evidence (hash_value);

CREATE INDEX idx_evidence_validation
    ON case_management.case_evidence (validation_status);

CREATE INDEX idx_evidence_confidentiality
    ON case_management.case_evidence (confidentiality_level);

CREATE INDEX idx_case_evidence_collected_by
    ON case_management.case_evidence (collected_by);