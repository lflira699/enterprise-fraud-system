-- EFS-DB-002
-- V114 - Align Transaction Attachment Checksum Type

ALTER TABLE transaction.transaction_attachment
    ALTER COLUMN checksum_sha256 TYPE VARCHAR(64);
