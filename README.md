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

Spring Boot service reserved for account, movement, and report capabilities.

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
