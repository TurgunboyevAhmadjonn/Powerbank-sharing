-- src/main/resources/db/changelog/migrations/V002__create_idempotency_keys.sql
CREATE TABLE idempotency_keys (
    key             VARCHAR(255)    PRIMARY KEY,
    response_body   TEXT            NOT NULL,
    status_code     INT             NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Auto-expire old keys after 24 h (cleaned up by a scheduled job or pg_cron)
CREATE INDEX idx_idempotency_created ON idempotency_keys (created_at);
