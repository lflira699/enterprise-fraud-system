ALTER TABLE integration.outbox_event
    ADD COLUMN processing_started_at TIMESTAMP;
