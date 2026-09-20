# Account Service Hexagonal Refactor

## Purpose

Refactor `account-service` to replicate the complete hexagonal architecture convention already established by `customer-service`, while preserving all current business behavior and external contracts.

The scope includes the account, movement, and account-report capabilities, plus the H2 test configuration required for the full Maven test suite to pass.

## Goals

- Establish explicit inbound and outbound application ports.
- Keep the application and domain core free from Spring, JPA, HTTP DTO, validation, and RabbitMQ dependencies.
- Separate account, movement, and report application services.
- Move transaction boundaries into infrastructure adapters/configuration.
- Make HTTP, persistence, and messaging adapters explicit.
- Preserve all current HTTP response shapes, routes, messages, and status codes.
- Preserve the existing customer-event payload and RabbitMQ routing contract.
- Keep account balance and movement business rules unchanged.
- Make the account-service H2 tests initialize the `account_service` schema correctly.

## Non-goals

- No new Maven module or service.
- No database schema or production SQL contract changes.
- No changes to `customer-service` behavior.
- No new account or movement features.
- No replacement of Spring Data, MapStruct, RabbitMQ, or the existing persistence technology.

## Target structure

```text
application
├── command
├── port
│   ├── in
│   └── out
├── result
└── service

domain
├── exception
└── model

infrastructure
├── adapter
│   ├── in
│   │   ├── messaging
│   │   └── web
│   │       ├── dto
│   │       └── mapper
│   └── out
│       ├── messaging
│       └── persistence
│           ├── adapter
│           ├── entity
│           ├── mapper
│           └── repository
└── config
```

The exact package names should follow the existing `customer-service` convention and the naming conventions already used by `account-service`.

## Architecture

### Application core

Create separate input ports and application services for:

- Account CRUD operations.
- Movement CRUD operations and balance adjustments.
- Account-statement report generation.
- Customer-event handling, if the existing listener performs application behavior that must be exposed through an inbound port.

Commands will represent use-case inputs and results will represent use-case outputs. Web request/response records remain inside the web adapter and are converted by web mappers.

The application services depend only on application ports and domain types. They must not import Spring annotations, HTTP DTOs, JPA entities, Spring Data repositories, or RabbitMQ classes.

### Domain

Keep `Cuenta`, `Movimiento`, `DatosCuenta`, `DatosMovimiento`, and domain exceptions framework-free. Preserve existing rules for:

- Required and non-negative account fields.
- Non-zero movement values.
- Deposit and withdrawal type derivation.
- Available-balance updates.
- Insufficient-funds rejection.
- Movement replacement and patch balance deltas.

### Inbound adapters

- HTTP controllers remain responsible for routing, validation annotations, HTTP status codes, and response envelopes.
- Web mappers convert HTTP DTOs to commands and results to response DTOs.
- The RabbitMQ customer-event listener remains responsible only for consuming and translating the external message into an application input-port call.

### Outbound adapters

- Persistence adapters implement application repository ports.
- JPA entities, Spring Data repositories, and persistence mappers remain under infrastructure.
- RabbitMQ configuration and any outbound messaging behavior remain under infrastructure.

### Transactions and configuration

Use a Spring infrastructure configuration equivalent to `customer-service`:

- Wire framework-free application services as beans.
- Expose transactional input-port adapters as the primary inbound-port beans.
- Keep `@Transactional` and `@Transactional(readOnly = true)` outside application services.
- Ensure the messaging listener invokes the appropriate application port rather than a framework-dependent use case.

## Preserved contracts

The following routes and behavior remain unchanged:

```text
/api/cuentas
/api/movimientos
/api/reportes
```

Preserve current request and response fields, API response envelopes, validation behavior, exception messages, HTTP statuses, report date-range semantics, and account/movement identifiers.

Preserve the customer-event JSON payload, exchange, routing key, queue/binding behavior, and listener semantics. Any compatibility mapping belongs in the messaging adapter.

## Data flow

```text
HTTP controller
  -> web mapper
  -> input port
  -> transactional input adapter
  -> application service
  -> output port
  -> persistence adapter
  -> JPA repository/entity
```

Movement creation and updates must continue to update the account balance and persist the movement within one transaction.

Report generation continues to query accounts by customer, retrieve movements within the inclusive date range, order them by date, and map them to the existing report response.

Customer events continue through:

```text
RabbitMQ listener
  -> inbound event port
  -> application handler/service
  -> account repository port
  -> persistence adapter
```

## Migration sequence

1. Establish application commands, results, ports, and service boundaries without changing behavior.
2. Move account use-case behavior behind the account input port.
3. Move movement use-case behavior behind the movement input port.
4. Move report behavior behind the report input port.
5. Relocate HTTP DTOs/controllers/mappers into the inbound web adapter.
6. Relocate JPA entities, repositories, mappers, and adapters into the outbound persistence adapter.
7. Relocate RabbitMQ listener/configuration/message mapping into messaging adapters.
8. Add infrastructure transaction/configuration wiring and remove direct Spring dependencies from the application core.
9. Add or adapt unit, adapter, HTTP, and messaging tests.
10. Fix H2 schema initialization for `account_service` and run focused and full verification.
11. Remove only obsolete empty or unused folders after references are verified.

## Testing strategy

- Domain tests verify account and movement invariants without Spring.
- Application-service tests use in-memory or fake output ports and do not load external adapters.
- Web integration tests verify routes, request mapping, response envelopes, validation, and error behavior.
- Persistence adapter tests verify domain/entity mapping and repository interaction using H2.
- Messaging tests verify customer-event compatibility and listener translation.
- `account-service` tests must initialize the H2 `account_service` schema explicitly where required.

Required gates:

```text
./mvnw -pl account-service test
./mvnw test
graphify update .
```

## Completion criteria

- `account-service` follows the same complete hexagonal convention as `customer-service`.
- The application and domain core contain no prohibited framework/external dependencies.
- Existing HTTP and RabbitMQ contracts are preserved.
- Account, movement, and report behavior remains covered by tests.
- `./mvnw -pl account-service test` passes.
- `./mvnw test` passes.
- No unsafe or unrelated files are removed.
