CREATE TABLE IF NOT EXISTS tb_outbox_event (
    id BINARY(16) NOT NULL,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id BINARY(16) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload JSON NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_tb_outbox_event PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE INDEX idx_tb_outbox_event_status_created
    ON tb_outbox_event (status, created_at);

CREATE INDEX idx_tb_outbox_event_type
    ON tb_outbox_event (event_type);
