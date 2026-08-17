CREATE TABLE integration.integration_queue (
    queue_id UUID NOT NULL DEFAULT uuidv7(),
    queue_name VARCHAR(150) NOT NULL,
    broker VARCHAR(100) NOT NULL,
    topic VARCHAR(150) NOT NULL,
    partition INTEGER,
    consumer_group VARCHAR(150),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_integration_queue
        PRIMARY KEY (queue_id)
);

CREATE INDEX idx_queue_name
    ON integration.integration_queue (queue_name);

CREATE INDEX idx_queue_broker
    ON integration.integration_queue (broker);