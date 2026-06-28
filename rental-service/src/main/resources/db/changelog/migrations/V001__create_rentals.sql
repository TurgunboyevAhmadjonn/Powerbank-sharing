-- src/main/resources/db/changelog/migrations/V001__create_rentals.sql
CREATE TYPE rental_status AS ENUM (
    'PENDING_STATION',
    'ACTIVE',
    'PENDING_PAYMENT',
    'COMPLETED',
    'CANCELLED',
    'FAILED'
);

CREATE TABLE rentals (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID            NOT NULL,
    station_id      UUID            NOT NULL,
    return_station_id UUID,
    slot_id         UUID            NOT NULL,
    return_slot_id  UUID,
    powerbank_id    UUID,
    card_id         UUID            NOT NULL,
    status          rental_status   NOT NULL DEFAULT 'PENDING_STATION',
    started_at      TIMESTAMPTZ,
    finished_at     TIMESTAMPTZ,
    total_cost      NUMERIC(12, 2),
    rate_per_hour   NUMERIC(8, 2)   NOT NULL DEFAULT 50.00,
    last_billed_at  TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Look up rentals by user for history endpoint (most recent first)
CREATE INDEX idx_rentals_user_id_created ON rentals (user_id, created_at DESC);

-- Filter by status — used by the recurring payment scheduler
CREATE INDEX idx_rentals_status ON rentals (status) WHERE status = 'ACTIVE';

-- Quick lookup when station returns an event referencing a powerbank
CREATE INDEX idx_rentals_powerbank_id ON rentals (powerbank_id) WHERE powerbank_id IS NOT NULL;
