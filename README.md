# Powerbank Sharing — MVP

A microservices backend for a powerbank sharing (rental) platform. Users can locate nearby stations, rent a powerbank, and return it at any station. Built as a Junior+ Java Backend Developer test assignment for Anor Accelerator.

---

## Architecture Overview

```
                        ┌─────────────────────────────────────────────────────┐
                        │              Kong API Gateway (DB-less)              │
                        │   OAuth2 Token Introspection → Keycloak             │
                        │   REST → gRPC Transcoding for station & rental      │
                        └──────────┬──────────┬──────────────┬────────────────┘
                                   │          │              │
                           gRPC    │   gRPC   │      gRPC    │
                                   ▼          ▼              ▼
┌─────────────────┐    ┌────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│  user-service   │    │ rental-service │  │ station-service  │  │ payment-service  │
│                 │    │                │  │                  │  │                  │
│ Phone OTP auth  │    │ Rental FSM     │  │ Station/PowerBank│  │ Card + Payments  │
│ Keycloak JWT    │    │ Recurring pay  │  │ Slot management  │  │ Idempotency      │
│ PostgreSQL      │    │ PostgreSQL     │  │ PostgreSQL       │  │ PostgreSQL       │
└─────────────────┘    └───────┬────────┘  └────────┬─────────┘  └──────────┬───────┘
                               │                    │                        │
                               └────────────────────┴────────────────────────┘
                                                    │
                                          ┌─────────▼─────────┐
                                          │       Kafka        │
                                          │                    │
                                          │ rental-commands    │
                                          │ station-events     │
                                          │ payment-requests   │
                                          │ payment-events     │
                                          └────────────────────┘
```

### Service Responsibilities

| Service | Role | Communication |
|---|---|---|
| `user-service` | Phone OTP registration, JWT issuance via Keycloak | REST (inbound) |
| `rental-service` | Rental lifecycle FSM, recurring payments | gRPC (inbound), Kafka (bidirectional) |
| `station-service` | Station/slot/powerbank management, IoT simulation | gRPC (inbound), Kafka (bidirectional) |
| `payment-service` | Card management, atomic payment processing | Kafka only |

---

## Tech Stack

- **Java 17** + **Spring Boot 3.2.5**
- **PostgreSQL 15** — one database per service
- **Apache Kafka** (KRaft mode, no ZooKeeper) — async service communication
- **gRPC** + **Protocol Buffers** — internal service contracts
- **Liquibase** — database migrations (no `ddl-auto: create`)
- **Kong Gateway** — API gateway with gRPC transcoding and OAuth2 introspection
- **Keycloak** — OAuth2/JWT authority
- **Docker Compose** — local development

---

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker + Docker Compose
- PostgreSQL 15 (if running services locally without Docker)

---

## Running with Docker Compose (recommended)

```bash
# Clone the repository
git clone https://github.com/TurgunboyevAhmadjonn/Powerbank-sharing.git
cd Powerbank-sharing

# Start all services
docker-compose up --build
```

Services will be available at:

| Service | Port | Protocol |
|---|---|---|
| Kong Gateway | `8000` | HTTP (REST) |
| rental-service gRPC | `9090` | gRPC |
| station-service gRPC | `9091` | gRPC |
| PostgreSQL | `5434` | TCP |
| Kafka | `9092` | TCP |

---

## Running Services Locally (without Docker)

### 1. Start infrastructure

```bash
# Start only Postgres and Kafka via Docker
docker-compose up postgres-db kafka -d
```

### 2. Create databases

```bash
psql -U postgres -c "CREATE DATABASE rental_db;"
psql -U postgres -c "CREATE DATABASE station_db;"
psql -U postgres -c "CREATE DATABASE payment_db;"
psql -U postgres -c "CREATE DATABASE user_db;"
```

### 3. Run each service

```bash
# Terminal 1 — User Service
cd user-service
mvn spring-boot:run

# Terminal 2 — Payment Service
cd payment-service
mvn spring-boot:run

# Terminal 3 — Station Service
cd station-service
mvn spring-boot:run

# Terminal 4 — Rental Service
cd rental-service
mvn spring-boot:run
```

---

## API Endpoints

All endpoints are exposed through Kong Gateway at `http://localhost:8000`. Without Kong, call services directly on their ports.

### User Service

```
POST /auth/phone          # Request OTP (sent via Telegram)
  Body: { "phone": "+998901234567" }

POST /auth/verify         # Verify OTP, receive JWT
  Body: { "phone": "+998901234567", "otp": "123456" }

POST /v1/auth/refresh     # Refresh JWT token
  Body: { "refreshToken": "..." }

GET  /v1/me               # Get current user profile
  Header: Authorization: Bearer <jwt>
```

### Rental Service

```
POST /v1/rental                   # Create rental (start powerbank checkout)
  Header: Authorization: Bearer <jwt>
  Body: { "stationId": "uuid", "cardId": "uuid" }
  Response: { "rentalId": "uuid" }

GET  /v1/rental/{id}/status       # Poll rental status
  Response: { "status": "WAITING|STATION_LOCKED|PAID|IN_THE_LEASE|RETURNED|FAILED",
              "powerbankId": "uuid", "slotNumber": 3 }

GET  /v1/rental/history           # List user's past rentals
  Header: Authorization: Bearer <jwt>

POST /v1/rental/finish            # Return powerbank
  Header: Authorization: Bearer <jwt>
  Body: { "rentalId": "uuid", "stationId": "uuid" }
```

### Station Service (via gRPC transcoding)

```
GET /v1/stations                  # Nearest stations
  Query: ?lat=41.33&lng=69.28&radius=5000&limit=20

GET /v1/stations/{id}             # Station details + slots
```

### Payment Service (Kafka only — no direct REST)

Payment Service has no REST endpoints. It communicates exclusively via Kafka:
- Consumes `payment-requests` topic
- Produces `payment-events` topic

---

## Kafka Topics

| Topic | Producer | Consumer | Purpose |
|---|---|---|---|
| `rental-commands` | rental-service | station-service | Lock cabinet, eject powerbank, return powerbank |
| `station-events` | station-service | rental-service | Results of station commands |
| `payment-requests` | rental-service | payment-service | Charge card for rental |
| `payment-events` | payment-service | rental-service | Payment result (success/failure) |

### Event Types

**rental-commands (rental → station):**
```json
{ "commandType": "ACQUIRE_CABINET_LOCK", "rentalId": "uuid", "stationId": "uuid" }
{ "commandType": "EJECT_POWERBANK",      "rentalId": "uuid", "stationId": "uuid" }
{ "commandType": "RETURN_POWERBANK",     "rentalId": "uuid", "stationId": "uuid", "powerbankId": "uuid" }
```

**station-events (station → rental):**
```json
{ "eventType": "CABINET_LOCK_RESULT",    "rentalId": "uuid", "success": true }
{ "eventType": "EJECT_POWERBANK_RESULT", "rentalId": "uuid", "success": true, "powerbankId": "uuid", "slotNumber": 3 }
{ "eventType": "RETURN_POWERBANK_RESULT","rentalId": "uuid", "success": true }
```

**payment-requests (rental → payment):**
```json
{ "rentalId": "uuid", "cardId": "uuid", "amount": 5000.00, "idempotencyKey": "uuid", "type": "INITIAL|RECURRING" }
```

**payment-events (payment → rental):**
```json
{ "rentalId": "uuid", "idempotencyKey": "uuid", "status": "SUCCEEDED|FAILED", "errorMessage": null }
```

---

## Rental Lifecycle (Sequence)

```
Client          Kong            rental-service      Kafka           station-service     payment-service
  │               │                   │               │                   │                   │
  │ POST /rental  │                   │               │                   │                   │
  │──────────────►│  gRPC CreateRental│               │                   │                   │
  │               │──────────────────►│               │                   │                   │
  │               │                   │ Create rental (WAITING)           │                   │
  │               │  rental_id        │               │                   │                   │
  │               │◄──────────────────│               │                   │                   │
  │ HTTP 200      │                   │               │                   │                   │
  │◄──────────────│                   │               │                   │                   │
  │               │                   │ ACQUIRE_CABINET_LOCK event        │                   │
  │               │                   │──────────────►│                   │                   │
  │               │                   │               │──────────────────►│                   │
  │               │                   │               │   Simulate lock   │                   │
  │               │                   │               │◄──────────────────│                   │
  │               │                   │ CABINET_LOCK_RESULT               │                   │
  │               │                   │◄──────────────│                   │                   │
  │               │                   │ FSM: WAITING → STATION_LOCKED     │                   │
  │               │                   │ payment-request event             │                   │
  │               │                   │──────────────►│                   │                   │
  │               │                   │               │──────────────────────────────────────►│
  │               │                   │               │                   │   Charge card     │
  │               │                   │               │◄──────────────────────────────────────│
  │               │                   │ payment-events SUCCEEDED          │                   │
  │               │                   │◄──────────────│                   │                   │
  │               │                   │ FSM: STATION_LOCKED → PAID        │                   │
  │               │                   │ EJECT_POWERBANK event             │                   │
  │               │                   │──────────────►│                   │                   │
  │               │                   │               │──────────────────►│                   │
  │               │                   │               │  Simulate eject   │                   │
  │               │                   │               │◄──────────────────│                   │
  │               │                   │ EJECT_POWERBANK_RESULT            │                   │
  │               │                   │◄──────────────│                   │                   │
  │               │                   │ FSM: PAID → IN_THE_LEASE          │                   │
  │               │                   │               │                   │                   │
  │ GET /rental/{id}/status           │               │                   │                   │
  │──────────────►│  gRPC GetStatus   │               │                   │                   │
  │               │──────────────────►│               │                   │                   │
  │               │ {IN_THE_LEASE}    │               │                   │                   │
  │◄──────────────│◄──────────────────│               │                   │                   │
```

---

## Database Schema

### user-service (`user_db`)
- `users` — id (UUID), phone (UNIQUE), otp_code, otp_expires_at (TIMESTAMPTZ), verified, created_at

### payment-service (`payment_db`)
- `cards` — id (UUID), user_id, balance (NUMERIC 19,4), card_number, created_at
- `payments` — id (UUID), card_id, rental_id, amount (NUMERIC 19,4), status, idempotency_key (UNIQUE), created_at

### station-service (`station_db`)
- `stations` — id (UUID), name, address, latitude, longitude, total_slots, available_slots, status, created_at
- `power_banks` — id (UUID), station_id, slot_number, status, battery_level, created_at
- `slots` — id (UUID), station_id, slot_number (UNIQUE per station), power_bank_id, status, created_at

### rental-service (`rental_db`)
- `rentals` — id (UUID), user_id, station_id, card_id, powerbank_id, slot_number, status, idempotency_key (UNIQUE), started_at (TIMESTAMPTZ), finished_at, last_charged_at
- `idempotency_keys` — key (UNIQUE), response_body, created_at

---

## What Is Implemented

| Feature | Status |
|---|---|
| User registration + OTP flow | ✅ Done (OTP printed to console, not Telegram) |
| JWT issuance | ⚠️ Fake JWT (Keycloak not integrated) |
| Card management (Payment Service) | ✅ Done |
| Atomic payment processing | ✅ Done |
| Idempotency key mechanism | ✅ Done |
| Payment Service Kafka consumer | ✅ Done |
| Station entities + Liquibase migrations | ✅ Done |
| Station gRPC service (GetNearestStations, GetStationById) | ✅ Done |
| Station Kafka consumer (lock + eject simulation) | ✅ Done |
| Rental Service FSM | ❌ Not implemented |
| Rental REST API | ❌ Not implemented |
| Recurring payments scheduler | ❌ Not implemented |
| Keycloak integration | ❌ Not implemented |
| Telegram OTP delivery | ❌ Not implemented |
| Kong Gateway configuration | ❌ Not implemented |
| Outbox Pattern (Kafka reliability) | ❌ Not implemented |
| Tests | ❌ Not written |
| DECISIONS.md | ✅ Complete |

See `DECISIONS.md` for full explanation of every decision and everything not completed.

---

## Project Structure

```
Powerbank-sharing/
├── docker-compose.yml
├── DECISIONS.md
├── README.md
├── user-service/
│   ├── pom.xml
│   └── src/main/java/com/anor/user/
│       ├── entity/User.java
│       ├── service/UserService.java (OTP generation + verification)
│       └── ...
├── payment-service/
│   ├── pom.xml
│   └── src/main/java/com/anor/payment/
│       ├── entity/{Card, Payment}.java
│       ├── service/PaymentService.java (atomic, idempotent)
│       ├── kafka/consumer/PaymentRequestConsumer.java
│       └── ...
├── station-service/
│   ├── pom.xml
│   ├── src/main/proto/station.proto
│   └── src/main/java/com/anor/station/
│       ├── entity/{Station, PowerBank, Slot}.java
│       ├── grpc/StationGrpcService.java
│       ├── kafka/consumer/RentalCommandConsumer.java
│       ├── service/StationDomainService.java
│       └── ...
└── rental-service/
    ├── pom.xml
    ├── src/main/proto/rental.proto
    └── src/main/java/com/anor/rental/
        ├── entity/Rental.java
        ├── service/RentalService.java
        └── ...
```

---

## Author

Ahmadjon Turgunboyev
Test assignment for Anor Accelerator — Java Backend Developer Junior+