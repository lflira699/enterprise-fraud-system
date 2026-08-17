ALTER TABLE integration.integration_retry
    RENAME COLUMN attempt_number TO retry_number;

ALTER TABLE integration.integration_retry
    RENAME COLUMN error_message TO error_description;

ALTER TABLE integration.integration_retry
    RENAME COLUMN next_attempt TO next_retry;

ALTER TABLE integration.integration_retry
    RENAME COLUMN status TO retry_status;

ALTER INDEX integration.idx_retry_next_attempt
    RENAME TO idx_retry_date;