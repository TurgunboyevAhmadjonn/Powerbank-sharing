# DECISIONS.md — Powerbank Sharing MVP

## Overview

This file documents the architectural decisions made during development of the Powerbank Sharing MVP. It explains what was built, why specific choices were made, and what remains to be done.

---

## 1. Architecture Decisions

### UUID vs BIGSERIAL for Primary Keys

**Decision: UUID (`GenerationType.UUID`)**

All entities (User, Card, Payment) use `UUID` as primary keys instead of auto-incrementing `BIGSERIAL`.

**Why:**
- In a microservices architecture, services generate IDs independently without coordinating with each other. If Payment Service and Rental Service both use BIGSERIAL, they each have their own sequence starting at 1, which causes ID collisions when data is joined or referenced across services.
- UUIDs are generated client-side (in the application), which means the service knows the ID before the database INSERT. This is important for Kafka — we can publish an event with the entity ID before or at the same time as saving to the database.
- UUIDs are harder to guess, which adds a small security benefit (a user cannot enumerate `/rentals/1`, `/rentals/2` to scrape data).

**Trade-off acknowledged:**
- UUID indexes are larger and slower than integer indexes on very large tables (100M+ rows). B-tree index on UUID has worse cache locality than sequential integers. For this MVP scale this is acceptable. At scale, ULIDs (sortable UUIDs) would be a better choice as they preserve insert order.

---

### NUMERIC / BigDecimal vs Double for Money

**Decision: `BigDecimal` in Java, `NUMERIC(19,4)` in PostgreSQL**

All financial amounts (card balance, payment amount) use `BigDecimal` in Java and `NUMERIC` type in the database.

**Why:**
- `Double` and `Float` use binary floating point representation (IEEE 754). Binary floating point cannot exactly represent most decimal fractions. For example: `0.1 + 0.2 = 0.30000000000000004` in floating point. For money this is unacceptable — rounding errors accumulate and you can lose or gain fractions of currency.
- `BigDecimal` uses arbitrary-precision decimal arithmetic. `0.1 + 0.2 = 0.3` exactly.
- PostgreSQL `NUMERIC(19,4)` stores the exact decimal value with 4 decimal places of precision.

---

### TIMESTAMPTZ vs TIMESTAMP

**Decision: `TIMESTAMPTZ` (timestamp with time zone) everywhere**

All timestamp columns use `TIMESTAMPTZ` in PostgreSQL, mapped to `OffsetDateTime` in Java.

**Why:**
- `TIMESTAMP` stores a local datetime with no timezone information. If the server moves to a different timezone, or if users are in different timezones, the stored times become ambiguous.
- `TIMESTAMPTZ` stores the moment in time as UTC internally, but accepts and returns values with timezone offset. This means the exact moment is always unambiguous regardless of server timezone.
- In a distributed system with services potentially deployed in different regions, `TIMESTAMPTZ` is the correct choice. OTP expiry (`otp_expires_at`), payment timestamps, and rental times must be compared across services — they must represent the same moment in time.
- Java's `OffsetDateTime` correctly maps to `TIMESTAMPTZ` and preserves the timezone offset.

---

### Index Decisions

**`users` table:**
- `UNIQUE INDEX` on `phone` — users are looked up by phone number on every OTP request and verification. Without this index, every auth request would be a full table scan. The UNIQUE constraint also enforces business rules (one account per phone).

**`payments` table:**
- `UNIQUE INDEX` on `idempotency_key` — explained in detail in the Idempotency section below. The database-level unique constraint is the last line of defense against duplicate payments even under race conditions.
- Index on `card_id` — payment history queries filter by card. Without an index this would scan the entire payments table.

**`cards` table:**
- Index on `user_id` — fetching a user's cards is a frequent operation (before every payment). Without index this scans all cards.

---

### Idempotency Key Design

**Decision: `idempotency_key` as UNIQUE column in payments table, checked before processing**

**Implementation in `PaymentService.processPayment()`:**

```
1. Query: SELECT * FROM payments WHERE idempotency_key = ?
2. If found AND amount matches → return existing payment (HTTP 200, not 201)
3. If found AND amount differs → throw error (conflict)
4. If not found → process normally and INSERT
```

**Return 200, not 201 on duplicate:**
- HTTP 201 means "a new resource was created." On a duplicate request, no new resource was created — we are returning an existing one. HTTP 200 is the correct response. The client receives the same payload as the first successful request.

**Same key, different amount:**
- This is rejected with an error (400 or 409). An idempotency key represents a specific intent — "charge X amount for rental Y." If the client sends the same key with a different amount, that is a client bug, not a retry. We should not silently process the different amount because it could cause incorrect charges.

**Why UNIQUE constraint at database level AND check in code?**
- The application-level check handles the 99.9% case.
- The database UNIQUE constraint handles the race condition: two requests with the same key arrive simultaneously, both pass the application check (before either INSERT commits), then one INSERT succeeds and one fails with a constraint violation. The application catches `DataIntegrityViolationException` and returns the existing payment.

---

### Atomic Financial Operations

**Decision: `@Transactional` wrapping balance check + deduction**

In `PaymentService.processPayment()`, the entire operation runs in a single database transaction:
1. Find card and lock it (SELECT FOR UPDATE would be ideal at scale)
2. Check balance >= amount
3. Deduct balance
4. Save card
5. Save payment with status

If any step fails, the transaction rolls back. This ensures the card balance and payment record are always consistent — you will never have a deducted balance without a payment record or a payment record without a balance deduction.

---

### Database Per Service

**Decision: Each microservice owns its own database schema**

Payment Service connects to its own database. User Service connects to its own database. They do not share tables.

**Why:**
- Shared databases create tight coupling. If User Service changes the `users` table structure, it could break Payment Service's queries.
- Independent databases allow each service to be deployed, scaled, and migrated independently.
- This follows the microservices principle of "database per service."

**Trade-off:**
- Cross-service queries require API calls or event messaging instead of SQL JOINs. This adds latency and complexity. For this MVP the simplicity of independent databases is worth it.

---

### Kafka over REST for Payment Communication

**Decision: Rental Service communicates with Payment Service only via Kafka, not REST**

**Why:**
- Payment processing is inherently asynchronous. The user does not need to wait for the bank response before getting confirmation that their rental started.
- If Payment Service is temporarily down, the payment request sits in Kafka and is processed when the service recovers. With REST, the rental would fail immediately.
- Kafka provides natural retry and backpressure mechanisms.
- Decoupling: Rental Service does not need to know Payment Service's URL or wait for its response.

---

### gRPC for Station Service

**Decision: Station Service exposes gRPC instead of REST**

**Why:**
- gRPC uses Protocol Buffers (binary format) which is smaller and faster to serialize/deserialize than JSON.
- gRPC provides a strongly-typed contract (`.proto` file). If the contract changes, both sides fail to compile, making breaking changes visible at build time rather than runtime.
- For internal service-to-service communication (Rental → Station), gRPC is more efficient than REST.
- Kong Gateway supports gRPC Transcoding, which means external REST clients can call gRPC services through the gateway without knowing gRPC exists.

---

## 2. Kafka Design

### What happens if Kafka is unavailable when sending a message?

**Current behavior:**
The `KafkaTemplate.send()` call will throw an exception (or return a failed Future). The payment record is already saved in the database at this point. This creates an inconsistency: the payment is `SUCCESS` in the database but no event was published to `payment-events`.

**Correct solution — Outbox Pattern (not yet implemented):**
The proper solution is the Transactional Outbox Pattern:
1. Inside the same database transaction that saves the `Payment`, also write a row to an `outbox` table: `{topic: "payment-events", payload: {...}, sent: false}`.
2. A separate background process (poller or CDC with Debezium) reads unsent outbox rows and publishes them to Kafka, then marks them as sent.
3. This guarantees: if the DB commit succeeds, the event WILL eventually be published. If the DB commit fails, no event is published.

**What I did instead (MVP):**
I wrapped the Kafka send in a try-catch. If Kafka is down, the error is logged but the payment is still saved. This means the event may be lost. In production this is unacceptable. I would implement the Outbox Pattern with more time.

---

### What to use as Kafka key — and why?

**Decision: `idempotency_key` as the Kafka message key**

In `PaymentEventListener`:
```java
kafkaTemplate.send("payment-events", result.getIdempotencyKey(), resultEvent);
```

**Why this matters for ordering:**
Kafka guarantees message ordering within a single partition. Messages with the same key are always routed to the same partition. This means all events for the same payment (e.g., `PENDING → SUCCESS → CANCELLED`) go to the same partition and are consumed in order.

If we used a random key or no key, events for the same payment could land in different partitions and be consumed out of order — the consumer might see `CANCELLED` before `SUCCESS`.

**Alternative considered:**
Using `rentalId` as the key (when the payment events are consumed by Rental Service). This would ensure all events for the same rental are ordered. This might be even better for the Rental Service FSM since it only cares about events for a specific rental. I chose `idempotency_key` because it is already guaranteed unique and ties directly to the payment record.

---

### Do we need transactionality between DB and Kafka?

**Yes, in production. No, in the current MVP.**

**The problem:**
```
1. Save payment to DB  ✓ (committed)
2. Send event to Kafka  ✗ (Kafka down / network error)
→ Payment exists in DB but no event published
→ Rental Service never knows the payment succeeded
→ Rental is stuck in WAITING state forever
```

**The solution I would implement:**
The Transactional Outbox Pattern (described above). This is the industry standard solution for exactly-once delivery guarantees between a database and a message broker.

**Why not Kafka transactions?**
Kafka does support transactions (Kafka Exactly Once Semantics). But this requires the producer to be transactional and does not help with the database side. The Outbox Pattern solves both sides atomically using only the database transaction.

---

## 3. What I Would Do With More Time

### Not completed in this submission:

**Station Service** — The folder structure and proto file were set up, but the full gRPC implementation with `StationServiceGrpc`, `Station` and `PowerBank` entities, Kafka-based async slot locking, and powerbank ejection simulation was not fully integrated.

**Rental Service** — The core orchestration service was not implemented. This includes:
- FSM (Finite State Machine) with states: `WAITING → LOCKED → PAID → IN_THE_LEASE → RETURNED`
- REST API: `POST /v1/rental`, `GET /v1/rental/{id}/status`, `GET /v1/rental/history`, `POST /v1/rental/finish`
- Publishing `acquire-cabinet-lock-event` to Kafka
- Consuming `acquire-cabinet-lock-result` and transitioning FSM
- Recurring payment scheduling

**Keycloak integration** — User Service currently returns a fake JWT token (`"fake-jwt-token-" + userId`). Real Keycloak integration would:
1. Call Keycloak Admin API to create a user on registration
2. Call Keycloak Token Endpoint to issue a real JWT after OTP verification
3. Kong Gateway would validate tokens via OAuth2 Token Introspection against Keycloak

**Telegram OTP** — OTP is currently printed to the console (`System.out.println`). Real implementation would use Telegram Bot API to send the OTP to the user's Telegram account linked to their phone number.

**Kong Gateway (DB-less mode with decK)** — Not configured. Would need:
- `kong.yml` declarative config file
- Services and routes for each microservice
- gRPC Transcoding for Station and Rental services
- OAuth2 Token Introspection plugin pointing to Keycloak

**Outbox Pattern** — Kafka event publishing is not transactionally safe. I understand the problem and the solution but did not have time to implement it.

**Liquibase on all services** — User Service has Liquibase migrations. Payment Service has a Liquibase changelog. Station and Rental services would need proper migration files. During development I used `ddl-auto: create-drop` with H2 to get services running, which is not production-safe.

**Tests** — No unit or integration tests were written. I would prioritize testing `PaymentService.processPayment()` (especially the idempotency edge cases) and the Rental Service FSM transitions.

**What I would do better:**

- Use `SELECT ... FOR UPDATE` (pessimistic locking) in `processPayment()` to prevent race conditions on the card balance under high concurrency
- Add proper error handling and dead-letter topics in Kafka consumers
- Add `@Retryable` on Kafka sends
- Use ULIDs instead of UUIDs for better index performance
- Add request validation (`@Valid`, `@NotNull`) on all DTOs
- Implement proper health checks (`/actuator/health`) for each service

---

## 4. Questions That Came Up

1. **The assignment says "stations respond asynchronously" — does this mean Station Service should never respond directly to Rental Service, only via Kafka?** I interpreted this as: Rental Service publishes a `lock-station` event, Station Service consumes it, simulates the lock, and publishes the result back. Rental Service FSM transitions on that result. But I am not 100% sure if a synchronous gRPC call from Rental to Station was intended for the initial lock, with Kafka only for the eject step.

2. **Recurring payments** — The assignment mentions recurring payments for active rentals. I understand this means a scheduled job (e.g., every 30 minutes) that charges the user's card for the ongoing rental. Should this be a `@Scheduled` method in Rental Service that publishes a new payment event each interval? Or should there be a separate billing cycle concept?

3. **When the same `idempotency_key` arrives but Kafka sends it twice (at-least-once delivery)** — The current idempotency check in `PaymentService` handles duplicate DB inserts. But if Kafka delivers the same message twice and the first processing is still in-flight, two threads could both pass the application-level idempotency check simultaneously before either commits. The database UNIQUE constraint catches this, but I am catching `DataIntegrityViolationException` and returning the existing record — is this the right behavior or should it be a 409?

4. **Kong gRPC Transcoding** — I understand the concept: Kong receives a REST request and translates it to a gRPC call using the `.proto` file. But I was not sure whether the `.proto` file needs to be uploaded to Kong or whether Kong uses reflection. I would research this further.

5. **Keycloak OTP flow** — The assignment says "OTP via Telegram." Keycloak has an OTP feature built-in, but it typically uses TOTP (Google Authenticator style), not SMS/Telegram. Should the OTP be generated and verified in our User Service (as I implemented), with Keycloak only issuing the JWT after we verify? Or should Keycloak be the one generating the OTP?

---


