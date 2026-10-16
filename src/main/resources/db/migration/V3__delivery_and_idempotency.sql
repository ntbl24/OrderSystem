CREATE TABLE idempotency_records (
    buyer_id varchar(255) NOT NULL,
    operation varchar(128) NOT NULL,
    request_key varchar(128) NOT NULL,
    request_hash char(64) NOT NULL,
    response_body text,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at timestamptz,
    PRIMARY KEY (buyer_id, operation, request_key),
    CHECK (octet_length(response_body) <= 262144)
);
CREATE TABLE outbox_events (
    id varchar(36) PRIMARY KEY,
    event_type varchar(128) NOT NULL,
    aggregate_id varchar(255) NOT NULL,
    payload text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    available_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delivered_at timestamptz,
    attempts integer NOT NULL DEFAULT 0 CHECK (attempts >= 0),
    last_error varchar(255)
);
CREATE INDEX ix_outbox_pending ON outbox_events(available_at, created_at, id)
    WHERE delivered_at IS NULL AND attempts < 10;
CREATE TABLE inbox_events (
    consumer varchar(128) NOT NULL,
    event_id varchar(36) NOT NULL,
    processed_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (consumer, event_id)
);
