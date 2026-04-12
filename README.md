# pruebadevsu

Phase 2 implementation for the Devsu banking microservices challenge.

## Modules

- `customer-service`: future owner of `Persona` and `Cliente`.
- `account-service`: future owner of `Cuenta`, `Movimiento`, and reports.

Both modules are Spring Boot services with the same clean architecture package layout:

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
  config
  messaging
  persistence
  web
```

## Ports

- `customer-service`: `8081`
- `account-service`: `8082`

## Validate Phase 1

```bash
./mvnw test
```

## Implemented In Phase 2

`customer-service` now includes:

- Domain models for `Persona` and `Cliente`.
- Repository port for `Cliente`.
- JPA persistence adapter for `Cliente`.
- REST CRUD endpoints at `/api/clientes`.
- Centralized exception handling.
- Unit test for the `Cliente` domain model.

RabbitMQ publishing is intentionally deferred to Phase 3.
