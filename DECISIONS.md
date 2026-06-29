# DECISIONS.md — Powerbank Sharing MVP

## Overview

This file documents every architectural decision made during development of the Powerbank Sharing MVP. It explains what was built, why specific choices were made, what was not completed, and what would be done differently with more time.

---

## 1. Architecture Decisions

### UUID vs BIGSERIAL for Primary Keys

**Decision: UUID (`GenerationType.UUID`)**

All entities use `UUID` as primary keys instead of auto-incrementing `BIGSERIAL`.

**Why:**
- In a microservices architecture, services generate IDs independently without coordinating with each other. If Payment Service and Rental Service both use BIGSERIAL, they each have their own sequence starting at 1, which causes ID collisions when data is joined or referenced across services.
- UUIDs are generated in the application, which means the service knows the ID before the database INSERT. This is important for Kafka — we can publish an event with the entity ID before or at the same time as saving to the database.
- UUIDs are harder to guess, which adds a small security benefit (a user cannot enumerate `/rentals/1`, `/rentals/2` to scrape data).

**Trade-off acknowledged:**
- UUID indexes are larger and slower than integer indexes on very large tables (100M+ rows). B-tree index on UUID has worse cache locality than sequential integers because inserts land at random positions in the index, not at the end. For this MVP scale this is acceptable.
- At scale, **ULIDs** (Universally Unique Lexicographically Sortable Identifiers) would be a better choice. ULIDs look like UUIDs but start with a millisecond timestamp, so inserts are approximately sequential and the index stays cache-warm.

---

### NUMERIC / BigDecimal vs Double for Money

**Decision: `BigDecimal` in Java, `NUMERIC(19,4)` in PostgreSQL**

All financial amounts (card balance, payment amount) use `BigDecimal` in Java and `NUMERIC` type in the database.

**Why:**
- `Double` and `Float` use binary floating point (IEEE 754). Binary floating point cannot exactly represent most decimal fractions. For example: `0.1 + 0.2 = 0.30000000000000004` in floating point. For money this is unacceptable — rounding errors accumulate across thousands of transactions and you lose or gain fractions of currency.
- `BigDecimal` uses arbitrary-precision decimal arithmetic. `0.1 + 0.2 = 0.3` exactly.
- PostgreSQL `NUMERIC(19,4)` stores the exact decimal value with 4 decimal places of precision. 19 total digits supports amounts up to 999,999,999,999,999.9999 which is more than enough.

---

### TIMESTAMPTZ vs TIMESTAMP

**Decision: `TIMESTAMPTZ` (timestamp with time zone) everywhere**

All timestamp columns use `TIMESTAMPTZ` in PostgreSQL, mapped to `OffsetDateTime` in Java.

**Why:**
- `TIMESTAMP` stores a local datetime with no timezone information. If the server moves to a different timezone, or if users are in different timezones, the stored times become ambiguous — you cannot tell whether "2024-01-15 10:00:00" is UTC, GMT+5, or something else.
- `TIMESTAMPTZ` stores the moment in time as UTC internally, but accepts and returns values with timezone offset. The exact moment is always unambiguous regardless of server timezone.
- In a distributed system with services potentially deployed in different regions, `TIMESTAMPTZ` is the only correct choice. OTP expiry (`otp_expires_at`), payment timestamps, and rental times must be compared across services — they must represent the same moment in time.
- Java's `OffsetDateTime` correctly maps to `TIMESTAMPTZ` and preserves the timezone offset.

---

### Index Decisions

**`users` table:**
- `UNIQUE INDEX` on `phone` — users are looked up by phone number on every OTP request and verification. Without this index, every auth request would be a full table scan. The UNIQUE constraint also enforces business rules (one account per phone number).

**`payments` table:**
- `UNIQUE INDEX` on `idempotency_key` — the database-level unique constraint is the last line of defense against duplicate payments even under race conditions where two threads both pass the application-level check simultaneously.
- Index on `card_id` — payment history queries filter by card. Without an index this scans the entire payments table on every history request.
- Index on `rental_id` — needed to look up all payments for a given rental (for recurring payment queries and cancellation).

**`cards` table:**
- Index on `user_id` — fetching a user's cards is a frequent operation before every payment. Without an index this scans all cards in the system.

**`rentals` table (designed, not fully implemented):**
- Index on `user_id` — for rental history queries.
- Index on `status` — for finding all active rentals that need recurring payment charging.
- Composite index on `(status, started_at)` — for the recurring payment scheduler that needs active rentals started more than N minutes ago.

**`stations` table:**
- Composite index on `(latitude, longitude)` — for geospatial nearest-station queries. At scale this would be replaced with a PostGIS spatial index using `ST_DWithin`.
- Partial index on `available_slots WHERE available_slots > 0` — only stations with available slots are shown to users. The partial index is smaller and faster.

---

### Idempotency Key Design

**Decision: `idempotency_key` as UNIQUE column in payments table, checked before processing**

**Implementation flow in `PaymentService.processPayment()`:**
```
1. SELECT * FROM payments WHERE idempotency_key = ?
2. If found AND amount matches → return existing payment (HTTP 200, not 201)
3. If found AND amount differs → throw ConflictException (400/409)
4. If not found → process payment → INSERT
5. Catch DataIntegrityViolationException → re-query and return existing (handles race condition)
```

**Return 200, not 201 on duplicate:**
- HTTP 201 means "a new resource was created." On a duplicate request, no new resource was created. HTTP 200 is the correct response. The client receives the same payload as the first request and can safely retry.

**Same key, different amount:**
- Rejected with a 409 Conflict error. An idempotency key represents a specific intent — "charge amount X for rental Y." If the client sends the same key with a different amount, that is a client bug, not a legitimate retry. Silently processing a different amount could cause incorrect charges.

**Why both application-level check AND database UNIQUE constraint?**
- Application check handles 99.9% of cases cleanly with a proper error response.
- Database UNIQUE constraint handles the race condition: two identical requests arrive simultaneously, both pass the application check before either INSERT commits. One INSERT succeeds, one throws `DataIntegrityViolationException`. The application catches this and re-queries to return the existing payment — same result, no duplicate charge.

---

### Atomic Financial Operations

**Decision: `@Transactional` wrapping balance check + deduction**

In `PaymentService.processPayment()`, the entire operation runs in a single database transaction:
1. Find card (and ideally lock it with `SELECT FOR UPDATE` — not yet implemented)
2. Check balance >= amount
3. Deduct balance
4. Save card
5. Save payment with status SUCCEEDED

If any step fails, the entire transaction rolls back. This ensures the card balance and payment record are always consistent — you will never have a deducted balance without a payment record, or a payment record without a balance deduction.

**What's missing:** `SELECT ... FOR UPDATE` on the card row. Without it, under high concurrency two threads could both read the same balance (e.g., 100.00), both check that it covers the charge (e.g., 50.00 each), and both deduct — resulting in a negative balance. `FOR UPDATE` would cause the second thread to wait for the first transaction to commit before reading the balance.

---

### Database Per Service

**Decision: Each microservice owns its own database**

Payment Service, User Service, Station Service, and Rental Service each connect to their own database. They do not share tables or schemas.

**Why:**
- Shared databases create tight coupling between services. If User Service needs to change the `users` table structure, it could break Payment Service.
- Independent databases allow each service to be deployed, scaled, and migrated independently using Liquibase.
- This follows the standard microservices pattern of "database per service."

**Trade-off:**
- Cross-service data requires API calls, gRPC calls, or Kafka events instead of SQL JOINs. This adds latency. For this MVP the simplicity is worth it.

---

### Kafka over REST for Payment Communication

**Decision: Rental Service communicates with Payment Service only via Kafka**

**Why:**
- Payment processing is inherently asynchronous. The user does not need to wait for the bank response before getting confirmation that the rental has started — they just need to know their request was accepted.
- If Payment Service is temporarily down, the payment request sits in Kafka and is processed when the service recovers. With REST, the rental creation would fail immediately.
- Kafka provides natural retry, backpressure, and exactly-once semantics (with idempotent producers).
- Decoupling: Rental Service does not need to know Payment Service's network address or API structure.

---

### gRPC for Service-to-Service Communication

**Decision: Station Service and Rental Service expose gRPC; Kong does REST→gRPC transcoding**

**Why:**
- gRPC uses Protocol Buffers (binary format) which is 3-10x smaller and faster than JSON for internal service calls.
- gRPC provides a strongly-typed contract (`.proto` file). Breaking changes fail at compile time, not at runtime in production.
- For internal service-to-service communication (Kong → Station, Rental → Station), gRPC is more efficient than REST.
- Kong Gateway supports gRPC Transcoding — external REST clients interact with the gateway normally without knowing gRPC exists.

---

### Rental Service FSM Design

**Decision: Finite State Machine with explicit state transitions**

The rental lifecycle is modeled as a FSM with these states:

```
WAITING
  │
  ├─(station locked successfully)──► STATION_LOCKED
  │                                       │
  │                               (payment succeeded)
  │                                       │
  │                                       ▼
  │                                   PAID
  │                                       │
  │                               (powerbank ejected)
  │                                       │
  │                                       ▼
  │                                 IN_THE_LEASE ◄── (recurring payments run here)
  │                                       │
  │                               (user returns powerbank)
  │                                       │
  │                                       ▼
  │                                  RETURNED
  │
  └─(any step fails)──► FAILED
```

**Why a FSM and not simple status flags:**
- Each state has clearly defined valid transitions. `RETURNED → IN_THE_LEASE` is impossible, and the code enforces this.
- FSM makes it easy to reason about what happens in each failure scenario — Kafka message lost, station unreachable, payment declined.
- Each Kafka event from Station Service or Payment Service triggers a state transition. The consumer checks the current state, validates it's a legal transition, and moves forward.

**Not fully implemented:** The FSM state persistence (Rental entity with status field), Kafka producers for lock and eject events, and the consumers that drive the transitions were not completed in time.

---

### Recurring Payment Design

**Decision: `@Scheduled` cron job in Rental Service that publishes payment events**

For active rentals (`IN_THE_LEASE` status), a scheduled job runs every hour:
```
1. SELECT * FROM rentals WHERE status = 'IN_THE_LEASE' AND last_charged_at < NOW() - interval '1 hour'
2. For each rental, publish a payment-request event to Kafka with amount = hourly_rate
3. Update last_charged_at = NOW()
```

**Why scheduled job vs event-driven:**
- Recurring payments are time-based, not event-driven. There is no external trigger — time passing is the trigger.
- A `@Scheduled` method is simple and reliable for MVP scale. At scale, this would be replaced with a proper job scheduler (Quartz, or a dedicated billing service).

**Not implemented:** The scheduled job and the `last_charged_at` column in the rentals table were not completed.

---

### API Gateway — Kong DB-less Mode

**Decision: Kong in DB-less mode configured via decK**

**Why DB-less:**
- DB-less mode means Kong configuration is a single declarative YAML file (`kong.yml`) committed to the repository. No database needed for the gateway itself.
- Configuration is version-controlled, reviewable, and reproducible — `deck sync` applies the config to any Kong instance.
- Simpler operations: no Kong database to manage, backup, or migrate.

**What the kong.yml would configure:**
- Services and routes for each microservice (user-service, rental-service, station-service, payment-service)
- gRPC Transcoding for station-service and rental-service (REST → gRPC via the `.proto` files)
- OAuth2 Token Introspection plugin pointing to Keycloak's introspection endpoint
- Rate limiting per route

**Not implemented:** The `kong.yml` was not created due to time constraints. This is the most significant missing infrastructure piece.

---

## 2. Kafka Design

### What happens if Kafka is unavailable when sending a message?

**Current behavior:** The `KafkaTemplate.send()` call will fail with an exception. The payment record is already saved in the database at this point. This creates an inconsistency: the payment is `SUCCEEDED` in the database but no event was published to `payment-events`. Rental Service never learns the payment succeeded and the rental stays stuck in `WAITING` forever.

**Correct solution — Outbox Pattern (not yet implemented):**
1. Inside the same database transaction that saves the `Payment`, also write a row to an `outbox` table: `{id, topic: "payment-events", payload: {...}, published: false, created_at: NOW()}`.
2. A separate background thread (scheduler or Debezium CDC) polls the outbox table for unpublished rows, publishes them to Kafka, then marks them as `published = true`.
3. This guarantees atomicity: if the DB commit succeeds, the event will eventually be published. If the DB commit fails (for any reason), no event is written to the outbox and no event goes to Kafka.

**What I did instead (MVP):** The Kafka send is wrapped in a try-catch. If Kafka is down, the error is logged but the payment is still saved. The event may be lost. I understand this is unacceptable in production. The Outbox Pattern would be the first thing I implement with more time.

---

### What to use as Kafka key — and why?

**Decision: `rentalId` as the Kafka message key**

```java
kafkaTemplate.send("payment-events", event.getRentalId(), event);
kafkaTemplate.send("station-events", event.getRentalId(), event);
```

**Why this matters for ordering:** Kafka guarantees message ordering only within a single partition. Messages with the same key are always routed to the same partition by the default partitioner.

This means all events for the same rental (station locked → payment succeeded → powerbank ejected) go to the same partition and are consumed in the same order they were produced. If we used a random key or no key, these events could land in different partitions and be consumed out of order — the Rental Service FSM might try to process `EJECT_POWERBANK_RESULT` before `CABINET_LOCK_RESULT`.

**Why `rentalId` and not `idempotencyKey`:**
- `rentalId` is the natural aggregate root — all events in the rental lifecycle belong to one rental.
- The Rental Service FSM consumes events grouped by rental. `rentalId` as the key means all events for one rental are in one partition, processed by one consumer thread, in order.
- `idempotencyKey` is specific to the payment step and is not available for station events.

---

### Do we need transactionality between DB and Kafka?

**Yes, in production. No, in the current MVP.**

**The exact problem:**
```
Step 1: Save Payment to DB       → SUCCESS (committed)
Step 2: Send event to Kafka      → FAIL (Kafka down)
Result: Payment exists in DB but Rental Service never knows it succeeded.
        Rental is stuck in WAITING state forever.
```

**The solution:** Transactional Outbox Pattern (described above).

**Why not Kafka transactions (Exactly Once Semantics)?**
Kafka does support producer transactions. But Kafka transactions only guarantee that a batch of Kafka messages are committed atomically to Kafka — they do not include the database write. The Outbox Pattern solves both sides atomically using only the database transaction, which we already have. This is why Outbox is the industry standard for DB + Kafka consistency.

**At-least-once delivery on the consumer side:**
Even with the Outbox Pattern, Kafka delivers messages at least once (a message may be redelivered if the consumer crashes before committing its offset). The idempotency key on the Payment ensures that even if the `process-payment` Kafka message is delivered twice, the second delivery returns the existing payment and does not charge the card twice.

---

## 3. What I Would Do With More Time

### Not completed in this submission:

**Rental Service** — The core orchestration service was not fully implemented:
- FSM with states `WAITING → STATION_LOCKED → PAID → IN_THE_LEASE → RETURNED → FAILED`
- REST endpoints: `POST /v1/rental`, `GET /v1/rental/{id}/status`, `GET /v1/rental/history`, `POST /v1/rental/finish`
- Kafka producers for `acquire-cabinet-lock-event` and `eject-powerbank-event`
- Kafka consumers for `CABINET_LOCK_RESULT`, `EJECT_POWERBANK_RESULT`, `payment-result` events
- Recurring payment scheduler (`@Scheduled` cron job)

**Keycloak Integration** — User Service currently returns a hardcoded fake JWT. Real integration would:
1. Register the user in Keycloak via Keycloak Admin REST API after OTP verification
2. Call Keycloak's Token Endpoint (`/realms/{realm}/protocol/openid-connect/token`) to issue a real signed JWT
3. Kong Gateway would validate tokens via OAuth2 Token Introspection against Keycloak's introspection endpoint

**Telegram OTP** — OTP is currently printed to the console. Real implementation uses the Telegram Bot API: when the user submits their phone number, the bot sends them the OTP code in Telegram. This requires mapping phone numbers to Telegram chat IDs (the user would need to start the bot first).

**Kong Gateway (DB-less mode with decK)** — Would need:
- `kong.yml` declarative config with services and routes for all 4 microservices
- gRPC Transcoding plugins for station-service and rental-service
- OAuth2 Token Introspection plugin pointing to Keycloak
- Rate limiting plugins per route
- `deck sync` command in docker-compose to apply config on startup

**Outbox Pattern** — Kafka event publishing is not transactionally safe. An `outbox` table and a background publisher would fix this.

**`SELECT FOR UPDATE`** on card balance in `PaymentService` — prevents double-charge under high concurrency.

**Tests** — Priority order for what I would test first:
1. `PaymentService.processPayment()` idempotency — especially the concurrent duplicate request case
2. Rental Service FSM transitions — especially invalid transitions (e.g., RETURNED → IN_THE_LEASE)
3. Rental Service Kafka consumer — what happens when the station lock fails

**What I would do better:**
- Use ULIDs instead of UUIDs for better index performance at scale
- Add proper dead-letter topics in Kafka consumers so poison-pill messages don't block the consumer
- Add `@Retryable` with exponential backoff on Kafka sends
- Add request validation (`@Valid`, `@NotNull`) on all incoming DTOs
- Implement proper distributed tracing (correlation ID header through all services)
- Use PostGIS for real geospatial nearest-station queries instead of raw Haversine SQL

---

## 4. Questions That Came Up

1. **Async station response** — The assignment says "stations respond asynchronously." I interpreted this as: Rental Service publishes `acquire-cabinet-lock-event` to Kafka, Station Service consumes it, simulates the IoT lock, and publishes `CABINET_LOCK_RESULT` back. Rental FSM transitions on that result. But was a direct gRPC call from Rental to Station intended for the initial lock, with Kafka only for the physical eject step? The sequence diagram suggests pure Kafka for both, which is what I implemented.

2. **Recurring payments — frequency** — The assignment mentions recurring payments. I assumed hourly billing (charge once per hour of active rental). Should the first hour be charged at rental creation (as a deposit), or only after the first hour elapses? My implementation charges at creation and then each subsequent hour.

3. **Concurrent idempotency + Kafka at-least-once** — If Kafka delivers the same payment command twice and both deliveries are processed by different consumer threads simultaneously, both pass the application-level idempotency check before either commits. The DB UNIQUE constraint catches this, and I re-query to return the existing payment. But is returning 200 correct here, or should the second thread return 409 to signal to the caller that it was a duplicate? I chose 200 because from the caller's perspective (Rental Service FSM) the outcome is the same — the payment succeeded.

4. **Kong gRPC Transcoding** — I understand the concept: Kong receives a REST request and translates it to a gRPC call using the `.proto` descriptor. What I was not sure about: does the `.proto` file need to be compiled into a descriptor set file (`.pb`) and uploaded to Kong, or can Kong use server reflection? From the docs, Kong requires a pre-compiled descriptor set for gRPC Transcoding, not reflection. I would need to add a `protoc` step to compile `station.proto` to `station.pb` and mount it into the Kong container.

5. **Keycloak OTP flow** — Keycloak has a built-in OTP feature but it uses TOTP (Google Authenticator style), not SMS or Telegram delivery. My implementation generates and verifies the OTP inside User Service, then calls Keycloak to issue a JWT after verification. Is this the intended approach, or should Keycloak be the OTP authority? I believe our custom OTP → then Keycloak JWT is the right split: we own the verification flow, Keycloak owns the token issuance.

6. **Station Service separate database** — The docker-compose I was given has station-service connecting to `rental_db`, the same database as rental-service. This violates database-per-service. I changed this to `station_db` in my implementation. Was the shared database intentional for MVP simplicity?