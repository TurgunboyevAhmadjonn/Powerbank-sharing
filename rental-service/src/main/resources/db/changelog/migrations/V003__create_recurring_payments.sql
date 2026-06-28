-- src/main/resources/db/changelog/migrations/V003__create_recurring_payments.sql
CREATE TYPE payment_charge_status AS ENUM (
    'PENDING',
    'SUCCESS',
    'FAILED'
);

CREATE TABLE recurring_payment_charges (
    id          UUID                    PRIMARY KEY DEFAULT gen_random_uuid(),
    rental_id   UUID                    NOT NULL REFERENCES rentals(id),
    amount      NUMERIC(12, 2)          NOT NULL,
    status      payment_charge_status   NOT NULL DEFAULT 'PENDING',
    kafka_msg_id VARCHAR(255),
    charged_at  TIMESTAMPTZ             NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_recurring_rental_id ON recurring_payment_charges (rental_id);
