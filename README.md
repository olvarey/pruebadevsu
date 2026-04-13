# Devsu Banking Microservices

Java/Spring Boot implementation for the Devsu banking microservices challenge.
The repository is organized as a Maven multi-module project with separate
services for customer management and account management.

## Technology Stack

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL driver
- RabbitMQ client through Spring AMQP
- Lombok
- MapStruct
- JUnit 5 and AssertJ
- H2 for tests
- Testcontainers dependencies for integration tests

## Project Structure

```text
.
├── pom.xml
├── customer-service
│   ├── pom.xml
│   └── src
└── account-service
    ├── pom.xml
    └── src
```

The root `pom.xml` is the parent Maven project. It defines shared versions and
build configuration for both services.

## Modules

### customer-service

Owns the customer API and the customer domain model:

- `Persona`
- `DatosPersona`
- `Cliente`

The service exposes customer CRUD operations at:

```text
/api/clientes
```

Default port:

```text
8081
```

### account-service

Owns the account and movement APIs:

- `Cuenta`
- `DatosCuenta`
- `Movimiento`
- `DatosMovimiento`

The service exposes account and movement CRU operations at:

```text
/api/cuentas
/api/movimientos
/api/reportes
```

Default port:

```text
8082
```

## Architecture

The code follows clean architecture boundaries inside each service:

```text
domain
  model
  exception
  repository
application
  dto
  mapper
  usecase
infrastructure
  persistence
  web
```

Dependency direction is inward:

```text
infrastructure -> application -> domain
```

The domain layer does not depend on Spring, JPA, web DTOs, or persistence
entities.

## customer-service Design

### Domain

The customer domain keeps validation and business state independent from
frameworks:

- `DatosPersona`: value object for personal data.
- `Persona`: base domain model for personal fields.
- `Cliente`: customer aggregate with `clienteId`, `contrasena`, and `estado`.
- Domain exceptions represent business validation failures.

The domain uses JavaBean getters so Java tooling and mappers can infer readable
properties without framework annotations in the domain model.

### Application

The application layer coordinates customer use cases:

- Create customer
- Get customer by `clienteId`
- List customers
- Replace customer
- Patch customer
- Deactivate customer

DTOs are located in `application.dto`. MapStruct is used in
`application.mapper` to map between API DTOs and domain objects.

### Infrastructure

The infrastructure layer contains adapters for external concerns:

- REST controller under `infrastructure.web`.
- JPA entities and Spring Data repository under `infrastructure.persistence`.
- A persistence adapter implementing the domain repository port.
- A dedicated MapStruct mapper for JPA entity/domain conversion.
- A global exception handler for consistent API error responses.

The persistence mapper is kept in infrastructure so the application layer does
not depend on JPA entity classes.

## Customer API

Base path:

```text
http://localhost:8081/api/clientes
```

Supported operations:

```text
POST   /api/clientes
GET    /api/clientes
GET    /api/clientes/{clienteId}
PUT    /api/clientes/{clienteId}
PATCH  /api/clientes/{clienteId}
DELETE /api/clientes/{clienteId}
```

Example create request:

```json
{
  "clienteId": "CLI-001",
  "nombre": "Jose Lema",
  "genero": "M",
  "edad": 35,
  "identificacion": "10001",
  "direccion": "Otavalo sn y principal",
  "telefono": "098254785",
  "contrasena": "1234",
  "estado": true
}
```

Example patch request:

```json
{
  "direccion": "Amazonas y NNUU",
  "telefono": "097548965"
}
```

## Account API

Account base path:

```text
http://localhost:8082/api/cuentas
```

Supported account operations:

```text
POST   /api/cuentas
GET    /api/cuentas
GET    /api/cuentas/{numeroCuenta}
PUT    /api/cuentas/{numeroCuenta}
PATCH  /api/cuentas/{numeroCuenta}
```

Example create account request:

```json
{
  "numeroCuenta": "478758",
  "tipoCuenta": "Ahorro",
  "saldoInicial": 2000.00,
  "estado": true,
  "clienteId": "CLI-001"
}
```

Movement base path:

```text
http://localhost:8082/api/movimientos
```

Supported movement operations:

```text
POST   /api/movimientos
GET    /api/movimientos
GET    /api/movimientos/{movimientoId}
PUT    /api/movimientos/{movimientoId}
PATCH  /api/movimientos/{movimientoId}
```

Example create movement request:

```json
{
  "numeroCuenta": "478758",
  "valor": -575.00
}
```

Movement values update the account balance. Positive values are deposits and
negative values are withdrawals. Withdrawals that exceed the current available
balance return `Saldo no disponible`.

Report base path:

```text
http://localhost:8082/api/reportes
```

Supported report operation:

```text
GET /api/reportes?fecha={startDate},{endDate}&cliente={clienteId}
```

The `fecha` query parameter uses an inclusive date range with `yyyy-MM-dd`
values.

Example account statement request:

```bash
curl "http://localhost:8082/api/reportes?fecha=2026-04-01,2026-04-30&cliente=CLI-001"
```

The response includes one row per movement:

```text
fecha
cliente
numeroCuenta
tipo
saldoInicial
estado
movimiento
saldoDisponible
```

## Configuration

### customer-service

Default configuration is in:

```text
customer-service/src/main/resources/application.yaml
```

Environment variables:

```text
CUSTOMER_DB_URL
CUSTOMER_DB_USERNAME
CUSTOMER_DB_PASSWORD
RABBITMQ_HOST
RABBITMQ_PORT
RABBITMQ_USERNAME
RABBITMQ_PASSWORD
```

Default database URL:

```text
jdbc:postgresql://localhost:5432/customers_db
```

### account-service

Default configuration is in:

```text
account-service/src/main/resources/application.yaml
```

Environment variables:

```text
ACCOUNT_DB_URL
ACCOUNT_DB_USERNAME
ACCOUNT_DB_PASSWORD
RABBITMQ_HOST
RABBITMQ_PORT
RABBITMQ_USERNAME
RABBITMQ_PASSWORD
```

Default database URL:

```text
jdbc:postgresql://localhost:5432/accounts_db
```

## Build And Test

Run all tests:

```bash
./mvnw test
```

Build all modules:

```bash
./mvnw clean package
```

## Run With Docker Compose

Build and start the full local environment:

```bash
docker compose up --build
```

This starts:

- `customer-service` on port `8081`
- `account-service` on port `8082`
- PostgreSQL for `customer-service` on local port `5433`
- PostgreSQL for `account-service` on local port `5434`
- RabbitMQ on local port `5672`
- RabbitMQ Management UI on local port `15672`

RabbitMQ Management UI:

```text
http://localhost:15672
```

Default credentials:

```text
guest / guest
```

Create a customer:

```bash
curl -i -X POST http://localhost:8081/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": "CLI-001",
    "nombre": "Jose Lema",
    "genero": "M",
    "edad": 35,
    "identificacion": "10001",
    "direccion": "Otavalo sn y principal",
    "telefono": "098254785",
    "contrasena": "1234",
    "estado": true
  }'
```

List customers:

```bash
curl http://localhost:8081/api/clientes
```

Create an account:

```bash
curl -i -X POST http://localhost:8082/api/cuentas \
  -H "Content-Type: application/json" \
  -d '{
    "numeroCuenta": "478758",
    "tipoCuenta": "Ahorro",
    "saldoInicial": 2000.00,
    "estado": true,
    "clienteId": "CLI-001"
  }'
```

Create a withdrawal movement:

```bash
curl -i -X POST http://localhost:8082/api/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "numeroCuenta": "478758",
    "valor": -575.00
  }'
```

Check the updated account balance:

```bash
curl http://localhost:8082/api/cuentas/478758
```

Generate an account statement report:

```bash
curl "http://localhost:8082/api/reportes?fecha=2026-04-01,2026-04-30&cliente=CLI-001"
```

Customer create, update, patch, and delete operations publish customer events to
RabbitMQ. `account-service` consumes those events from the
`account.customer.events` queue and logs the received payload.

Stop the environment:

```bash
docker compose down
```

Stop the environment and remove database volumes:

```bash
docker compose down -v
```

Run `customer-service` locally:

```bash
./mvnw -pl customer-service spring-boot:run
```

Run `account-service` locally:

```bash
./mvnw -pl account-service spring-boot:run
```

## Notes

- The services expect PostgreSQL and RabbitMQ configuration through environment
  variables when running outside tests.
- Tests use H2 in PostgreSQL compatibility mode.
- `spring.jpa.hibernate.ddl-auto` is configured as `update` for local
  development.
