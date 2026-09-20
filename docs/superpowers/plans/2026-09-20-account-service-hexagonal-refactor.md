# Account Service Hexagonal Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor `account-service` to replicate the complete hexagonal convention of `customer-service` while preserving its HTTP, RabbitMQ, persistence, and business contracts.

**Architecture:** Move use-case inputs and outputs into framework-free application commands, results, and ports. Keep account and movement rules in the domain, place Spring transaction boundaries in infrastructure, and expose HTTP, persistence, and messaging through explicit adapters.

**Tech Stack:** Java 17, Spring Boot 3.3.5, Spring Web, Spring Data JPA, Spring AMQP/RabbitMQ, MapStruct, Jakarta Validation, JUnit 5, AssertJ, H2, Maven.

**Spec:** `docs/superpowers/specs/2026-09-20-account-service-hexagonal-design.md`

## Global Constraints

- Keep `account-service` in the existing Maven module; do not add modules or services.
- Keep the application and domain core free from Spring, JPA, HTTP DTO, validation, and RabbitMQ dependencies.
- Preserve `/api/cuentas`, `/api/movimientos`, and `/api/reportes` contracts, response envelopes, statuses, validation messages, and exception messages.
- Preserve the customer-event JSON payload, exchange, queue, routing key, binding, and listener semantics.
- Keep account balance, movement, insufficient-funds, replacement, patch, and report rules unchanged.
- Do not change `customer-service` behavior or production database SQL.
- Keep JPA entities, Spring Data repositories, MapStruct infrastructure mappers, and RabbitMQ classes under `infrastructure`.
- Put `@Transactional` and `@Transactional(readOnly = true)` on infrastructure input adapters, not application services.
- Remove only obsolete files after compilation and reference searches prove they are unused.

## Review Focus

- A negative movement must reject insufficient funds without persisting either balance or movement; pin this in the movement application-service test in Task 3.
- Replacing or patching a movement must apply only the value delta and preserve the current account association; pin this in Task 3.
- Account replacement must preserve `saldoDisponible` while changing editable fields; pin this in the account application-service test in Task 2.
- Reports must include inclusive date boundaries, sort by movement date, and return an empty list for a customer with no accounts; pin this in the report service test in Task 4.
- H2 tests must create and use `account_service` before Hibernate creates tables; pin this in the context and HTTP integration tests in Task 1 and Task 8.

---

### Task 1: Establish characterization coverage and H2 schema setup

**Files:**
- Modify: `account-service/src/test/java/com/devsu/accountservice/AccountServiceApplicationTests.java`
- Modify: `account-service/src/test/java/com/devsu/accountservice/infrastructure/web/AccountWorkflowIntegrationTest.java`
- Modify: `account-service/src/test/java/com/devsu/accountservice/application/usecase/ReporteUseCaseTest.java` (move or rename package only after replacement tests exist)
- Create: `account-service/src/test/java/com/devsu/accountservice/application/service/AccountApplicationServiceTest.java`
- Create: `account-service/src/test/java/com/devsu/accountservice/application/service/MovementApplicationServiceTest.java`
- Create: `account-service/src/test/java/com/devsu/accountservice/application/service/ReportApplicationServiceTest.java`

**Interfaces:**
- Consumes: existing endpoint behavior, domain models, repository contracts, and current integration workflow.
- Produces: executable behavior snapshots that protect the refactor before old use cases and DTOs are removed.

- [ ] **Step 1: Add the H2 schema initialization to both Spring Boot test contexts.**

  Use the same URL pattern already proven by `customer-service`:

  ```text
  jdbc:h2:mem:account_service_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS account_service
  ```

  Apply the equivalent `INIT=CREATE SCHEMA IF NOT EXISTS account_service` suffix to the workflow test database URL.

- [ ] **Step 2: Run the existing account-service tests and verify the schema failure is gone.**

  Run:

  ```bash
  ./mvnw -pl account-service test
  ```

  Expected: the context loads and the existing workflow test executes; any later failures identify behavior that must be preserved during migration.

- [ ] **Step 3: Add application-service test fixtures with fake output ports.**

  Define in-memory `CuentaRepository` and `MovimientoRepository` implementations in the new tests, using the same exact methods as the future `application.port.out` interfaces. Do not load Spring or use HTTP DTOs.

- [ ] **Step 4: Add tests for the currently implemented account behavior.**

  Cover duplicate account rejection, account lookup failure, replacement preserving `saldoDisponible`, patching editable account fields, and list mapping.

- [ ] **Step 5: Add tests for the currently implemented movement behavior.**

  Cover create balance updates, insufficient-funds atomicity, replacement delta adjustment, patch delta adjustment, missing account, missing movement, and account-number mismatch.

- [ ] **Step 6: Add tests for the currently implemented report behavior.**

  Cover inclusive start/end dates, chronological ordering, customer filtering, and empty results when no accounts belong to the customer.

- [ ] **Step 7: Run the focused tests and commit the characterization baseline.**

  Run:

  ```bash
  ./mvnw -pl account-service -Dtest='*ApplicationServiceTest,*ReportApplicationServiceTest,AccountWorkflowIntegrationTest,AccountServiceApplicationTests' test
  ```

  Commit:

  ```bash
  git add account-service/src/test
  git commit -m "test: characterize account service behavior"
  ```

### Task 2: Create account application ports, commands, results, and service

**Files:**
- Create: `account-service/src/main/java/com/devsu/accountservice/application/command/CreateCuentaCommand.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/command/ReplaceCuentaCommand.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/command/PatchCuentaCommand.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/result/CuentaResult.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/port/in/CuentaInputPort.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/port/out/CuentaRepository.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/service/CuentaApplicationService.java`
- Modify: `account-service/src/test/java/com/devsu/accountservice/application/service/AccountApplicationServiceTest.java`
- Delete after migration: `account-service/src/main/java/com/devsu/accountservice/application/usecase/CuentaUseCase.java`

**Interfaces:**
- Consumes: framework-free `Cuenta`, `CuentaDuplicadaException`, `CuentaNoEncontradaException`, and the characterization tests from Task 1.
- Produces:
  - `CuentaInputPort.create(CreateCuentaCommand)`
  - `CuentaInputPort.get(String)`
  - `CuentaInputPort.list()`
  - `CuentaInputPort.replace(String, ReplaceCuentaCommand)`
  - `CuentaInputPort.patch(String, PatchCuentaCommand)`
  - `CuentaRepository.existsByNumeroCuenta`, `findByNumeroCuenta`, `findAll`, `findByClienteId`, and `save`
  - `CuentaApplicationService` with no Spring annotations or HTTP imports.

- [ ] **Step 1: Write port and command/result types.**

  Commands must contain only the fields required by the use case:

  ```java
  public record CreateCuentaCommand(
      String numeroCuenta, String tipoCuenta, BigDecimal saldoInicial,
      boolean estado, String clienteId) {}
  ```

  Use equivalent complete and patch records. `CuentaResult` must contain the current response fields without validation annotations.

- [ ] **Step 2: Move account behavior into `CuentaApplicationService`.**

  Inject only `CuentaRepository`. Convert commands to `Cuenta` inside the service, return `CuentaResult`, keep duplicate and missing-account checks, and preserve replacement/patch balance semantics.

- [ ] **Step 3: Update the account unit tests to call the input port/service directly.**

  Assert the exact result values and exception messages from the characterization cases. The test must compile without `@SpringBootTest`.

- [ ] **Step 4: Run the account application tests.**

  Run:

  ```bash
  ./mvnw -pl account-service -Dtest=AccountApplicationServiceTest test
  ```

  Expected: PASS with the new service and no dependency on the old use case.

- [ ] **Step 5: Commit the account application core.**

  ```bash
  git add account-service/src/main/java/com/devsu/accountservice/application account-service/src/test/java/com/devsu/accountservice/application/service/AccountApplicationServiceTest.java
  git commit -m "refactor: isolate account application core"
  ```

### Task 3: Create movement application ports, commands, results, and service

**Files:**
- Create: `account-service/src/main/java/com/devsu/accountservice/application/command/CreateMovimientoCommand.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/command/ReplaceMovimientoCommand.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/command/PatchMovimientoCommand.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/result/MovimientoResult.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/port/in/MovimientoInputPort.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/port/out/MovimientoRepository.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/service/MovimientoApplicationService.java`
- Modify: `account-service/src/test/java/com/devsu/accountservice/application/service/MovementApplicationServiceTest.java`
- Delete after migration: `account-service/src/main/java/com/devsu/accountservice/application/usecase/MovimientoUseCase.java`

**Interfaces:**
- Consumes: `CuentaRepository`, `MovimientoRepository`, `Cuenta`, `Movimiento`, and the existing movement domain rules.
- Produces:
  - `MovimientoInputPort.create(CreateMovimientoCommand)`
  - `MovimientoInputPort.get(String)`
  - `MovimientoInputPort.list()`
  - `MovimientoInputPort.replace(String, ReplaceMovimientoCommand)`
  - `MovimientoInputPort.patch(String, PatchMovimientoCommand)`
  - `MovimientoRepository.findByMovimientoId`, `findAll`, `findByNumeroCuentaInAndFechaBetween`, and `save`

- [ ] **Step 1: Write movement commands and result types without Jakarta Validation or Spring imports.**

  Preserve nullable patch fields and the existing default-date behavior in the domain.

- [ ] **Step 2: Move movement mapping and UUID generation into the application service.**

  The service creates a UUID for new movements, derives `tipoMovimiento` through `DatosMovimiento.tipoPara`, updates the account before saving the movement, and returns `MovimientoResult`.

- [ ] **Step 3: Preserve delta and atomicity behavior.**

  Keep the current sequence and transaction expectation: find account, apply/adjust balance, save account, calculate resulting movement balance, then save movement. Let `SaldoNoDisponibleException` abort the transaction.

- [ ] **Step 4: Update movement tests for the new ports and service.**

  Pin insufficient withdrawal non-persistence, replacement delta, patch delta, same-account validation, and missing aggregate errors.

- [ ] **Step 5: Run and commit the movement core.**

  Run:

  ```bash
  ./mvnw -pl account-service -Dtest=MovementApplicationServiceTest test
  ```

  Commit:

  ```bash
  git add account-service/src/main/java/com/devsu/accountservice/application account-service/src/test/java/com/devsu/accountservice/application/service/MovementApplicationServiceTest.java
  git commit -m "refactor: isolate movement application core"
  ```

### Task 4: Create report application port, result, and service

**Files:**
- Create: `account-service/src/main/java/com/devsu/accountservice/application/result/EstadoCuentaResult.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/port/in/ReporteInputPort.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/application/service/ReporteApplicationService.java`
- Modify: `account-service/src/test/java/com/devsu/accountservice/application/service/ReportApplicationServiceTest.java`
- Delete after migration: `account-service/src/main/java/com/devsu/accountservice/application/usecase/ReporteUseCase.java`

**Interfaces:**
- Consumes: `CuentaRepository`, `MovimientoRepository`, `Cuenta`, `Movimiento`, and `DomainException`.
- Produces: `ReporteInputPort.getEstadoCuenta(String fecha, String clienteId)` returning `List<EstadoCuentaResult>`.

- [ ] **Step 1: Move `EstadoCuentaResponse` fields into a framework-free result record.**

  Keep the field names and types needed by the existing JSON response: `fecha`, `cliente`, `numeroCuenta`, `tipo`, `saldoInicial`, `estado`, `movimiento`, and `saldoDisponible`.

- [ ] **Step 2: Move report parsing and assembly into `ReporteApplicationService`.**

  Preserve the `yyyy-MM-dd,yyyy-MM-dd` parser, inclusive end-of-day behavior, validation messages, customer account lookup, chronological ordering, and empty-list behavior.

- [ ] **Step 3: Update report tests to use the new result and service.**

  Include boundary dates and a customer with no accounts.

- [ ] **Step 4: Run and commit the report core.**

  Run:

  ```bash
  ./mvnw -pl account-service -Dtest=ReportApplicationServiceTest test
  ```

  Commit:

  ```bash
  git add account-service/src/main/java/com/devsu/accountservice/application account-service/src/test/java/com/devsu/accountservice/application/service/ReportApplicationServiceTest.java
  git commit -m "refactor: isolate report application core"
  ```

### Task 5: Move web DTOs and controllers to the inbound adapter

**Files:**
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/ApiResponse.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/GlobalExceptionHandler.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/CuentaController.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/MovimientoController.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/ReporteController.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/dto/CuentaPatchRequest.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/dto/CuentaRequest.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/dto/CuentaResponse.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/dto/EstadoCuentaResponse.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/dto/MovimientoPatchRequest.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/dto/MovimientoRequest.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/dto/MovimientoResponse.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/mapper/CuentaWebMapper.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/mapper/MovimientoWebMapper.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web/mapper/ReporteWebMapper.java`
- Delete after migration: `account-service/src/main/java/com/devsu/accountservice/infrastructure/web/*.java`
- Delete after migration: `account-service/src/main/java/com/devsu/accountservice/application/dto/*.java`
- Delete after migration: `account-service/src/main/java/com/devsu/accountservice/application/mapper/*.java`

**Interfaces:**
- Consumes: the three input ports and application commands/results from Tasks 2–4.
- Produces: the same public REST endpoints, request validation, response JSON, error body, status codes, and Spanish messages.

- [ ] **Step 1: Copy DTO records into the inbound web package and retain validation annotations only there.**

  Do not import `jakarta.validation` into commands or results.

- [ ] **Step 2: Implement web mappers.**

  Convert `CuentaRequest`, `CuentaPatchRequest`, `MovimientoRequest`, and `MovimientoPatchRequest` into commands. Convert results into response records without changing field names or JSON shape.

- [ ] **Step 3: Change controllers to depend on input ports only.**

  Preserve route annotations, status handling, success messages, and method behavior. Controllers must not depend on application DTOs or use-case classes.

- [ ] **Step 4: Move the global exception handler and response envelope.**

  Preserve handlers for not-found, duplicate account, insufficient funds, domain validation, and bean-validation errors exactly.

- [ ] **Step 5: Run the HTTP workflow integration test.**

  Run:

  ```bash
  ./mvnw -pl account-service -Dtest=AccountWorkflowIntegrationTest test
  ```

  Expected: the existing create-account, withdrawal, deposit, balance, insufficient-funds, and report assertions pass through the new adapters.

- [ ] **Step 6: Commit the inbound web adapter.**

  ```bash
  git add account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/web account-service/src/test/java/com/devsu/accountservice/infrastructure/web/AccountWorkflowIntegrationTest.java
  git commit -m "refactor: isolate account HTTP adapter"
  ```

### Task 6: Move persistence into outbound adapters and relocate repository ports

**Files:**
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/out/persistence/adapter/CuentaPersistenceAdapter.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/out/persistence/adapter/MovimientoPersistenceAdapter.java`
- Move: `account-service/src/main/java/com/devsu/accountservice/infrastructure/persistence/entity/*.java` to `infrastructure/adapter/out/persistence/entity/`
- Move: `account-service/src/main/java/com/devsu/accountservice/infrastructure/persistence/mapper/*.java` to `infrastructure/adapter/out/persistence/mapper/`
- Move: `account-service/src/main/java/com/devsu/accountservice/infrastructure/persistence/repository/*.java` to `infrastructure/adapter/out/persistence/repository/`
- Create: `account-service/src/test/java/com/devsu/accountservice/infrastructure/adapter/out/persistence/adapter/CuentaPersistenceAdapterTest.java`
- Create: `account-service/src/test/java/com/devsu/accountservice/infrastructure/adapter/out/persistence/adapter/MovimientoPersistenceAdapterTest.java`
- Delete after migration: `account-service/src/main/java/com/devsu/accountservice/infrastructure/persistence/adapter/*.java`
- Delete after migration: `account-service/src/main/java/com/devsu/accountservice/domain/repository/*.java`

**Interfaces:**
- Consumes: `application.port.out.CuentaRepository`, `application.port.out.MovimientoRepository`, domain models, and existing JPA mappings.
- Produces: persistence implementations with the same repository operations and no application dependency on JPA types.

- [ ] **Step 1: Create application output ports with the existing repository method signatures.**

  Keep query semantics and return types identical, including the inclusive movement date range.

- [ ] **Step 2: Move entities, Spring Data repositories, and MapStruct entity mappers under the outbound persistence adapter.**

  Keep table names, columns, schema configuration, and generated mapper behavior unchanged.

- [ ] **Step 3: Implement persistence adapters against the new ports.**

  Preserve mapping of all account and movement fields and all repository query methods.

- [ ] **Step 4: Add adapter tests.**

  Use H2 to verify save/find/list/customer filtering/date-range queries and domain/entity round trips.

- [ ] **Step 5: Run persistence and account-service tests.**

  Run:

  ```bash
  ./mvnw -pl account-service test
  ```

- [ ] **Step 6: Commit the outbound persistence adapter.**

  ```bash
  git add account-service/src/main/java/com/devsu/accountservice/application/port/out account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/out/persistence account-service/src/test/java/com/devsu/accountservice/infrastructure/adapter/out/persistence
  git commit -m "refactor: isolate account persistence adapter"
  ```

### Task 7: Relocate messaging and preserve customer-event compatibility

**Files:**
- Move: `account-service/src/main/java/com/devsu/accountservice/infrastructure/messaging/ClienteEventMessage.java` to `infrastructure/adapter/in/messaging/`
- Move: `account-service/src/main/java/com/devsu/accountservice/infrastructure/messaging/ClienteEventListener.java` to `infrastructure/adapter/in/messaging/`
- Move: `account-service/src/main/java/com/devsu/accountservice/infrastructure/messaging/CustomerEventMessagingConfig.java` to `infrastructure/adapter/in/messaging/`
- Move: `account-service/src/main/java/com/devsu/accountservice/infrastructure/messaging/CustomerEventMessagingProperties.java` to `infrastructure/adapter/in/messaging/`
- Create: `account-service/src/test/java/com/devsu/accountservice/infrastructure/adapter/in/messaging/ClienteEventCompatibilityTest.java`
- Delete after migration: `account-service/src/main/java/com/devsu/accountservice/infrastructure/messaging/*.java`

**Interfaces:**
- Consumes: the existing customer event JSON payload and application startup configuration.
- Produces: the same exchange, durable queue, binding, routing key, JSON conversion, listener queue, and logging semantics.

- [ ] **Step 1: Move message records and RabbitMQ configuration to the inbound messaging adapter package.**

  Keep property prefix `app.messaging.customer-events` and all bean names/values used by the deployment configuration.

- [ ] **Step 2: Keep the listener behavior unchanged while making its adapter boundary explicit.**

  Since the current listener only logs received events and does not mutate account state, do not invent a new domain operation. Preserve the logger fields and message text while placing RabbitMQ concerns exclusively in the adapter.

- [ ] **Step 3: Add a compatibility test for deserialization.**

  Deserialize a representative customer event containing `eventId`, `eventType`, `occurredAt`, `clienteId`, `nombre`, `identificacion`, and `estado`, and assert every field survives unchanged.

- [ ] **Step 4: Run messaging and account-service tests, then commit.**

  Run:

  ```bash
  ./mvnw -pl account-service test
  ```

  Commit:

  ```bash
  git add account-service/src/main/java/com/devsu/accountservice/infrastructure/adapter/in/messaging account-service/src/test/java/com/devsu/accountservice/infrastructure/adapter/in/messaging
  git commit -m "refactor: isolate customer event messaging adapter"
  ```

### Task 8: Add infrastructure wiring and transaction boundaries

**Files:**
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/config/AccountServiceConfiguration.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/config/TransactionalCuentaInputAdapter.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/config/TransactionalMovimientoInputAdapter.java`
- Create: `account-service/src/main/java/com/devsu/accountservice/infrastructure/config/TransactionalReporteInputAdapter.java`
- Modify: `account-service/src/main/java/com/devsu/accountservice/AccountServiceApplication.java` only if package scanning requires it
- Modify: `account-service/src/test/java/com/devsu/accountservice/AccountServiceApplicationTests.java`

**Interfaces:**
- Consumes: application services and ports from Tasks 2–4 plus outbound persistence adapters from Task 6.
- Produces: Spring beans exposing the primary input ports with transaction metadata outside the application core.

- [ ] **Step 1: Wire framework-free services in `AccountServiceConfiguration`.**

  Declare beans for `CuentaApplicationService`, `MovimientoApplicationService`, and `ReporteApplicationService` using output ports.

- [ ] **Step 2: Add transactional input adapters.**

  Delegate every method to its service and preserve transaction modes:

  ```java
  @Transactional
  create/replace/patch

  @Transactional(readOnly = true)
  get/list/report
  ```

  Use `@Primary` on the exposed input-port beans so controllers receive the transactional adapters.

- [ ] **Step 3: Verify core dependency boundaries.**

  Run:

  ```bash
  grep -RInE 'org\.springframework|jakarta\.persistence|jakarta\.validation|org\.springframework\.amqp' account-service/src/main/java/com/devsu/accountservice/application account-service/src/main/java/com/devsu/accountservice/domain
  ```

  Expected: no matches.

- [ ] **Step 4: Run context and HTTP integration tests.**

  Run:

  ```bash
  ./mvnw -pl account-service test
  ```

  Expected: all beans resolve, H2 schema initialization succeeds, RabbitMQ listeners remain disabled in tests, and the HTTP workflow passes.

- [ ] **Step 5: Commit infrastructure wiring.**

  ```bash
  git add account-service/src/main/java/com/devsu/accountservice/infrastructure/config account-service/src/test/java/com/devsu/accountservice/AccountServiceApplicationTests.java
  git commit -m "refactor: add account service hexagonal wiring"
  ```

### Task 9: Remove legacy packages, document public APIs, and validate the module

**Files:**
- Delete: obsolete files under `account-service/src/main/java/com/devsu/accountservice/application/usecase/`
- Delete: obsolete files under `account-service/src/main/java/com/devsu/accountservice/application/dto/`
- Delete: obsolete files under `account-service/src/main/java/com/devsu/accountservice/application/mapper/`
- Delete: obsolete files under `account-service/src/main/java/com/devsu/accountservice/infrastructure/web/`
- Delete: obsolete files under `account-service/src/main/java/com/devsu/accountservice/infrastructure/persistence/`
- Delete: obsolete files under `account-service/src/main/java/com/devsu/accountservice/infrastructure/messaging/`
- Modify: public commands, results, ports, application services, controllers, mappers, adapters, and configuration methods under `account-service/src/main/java/` for Javadoc coverage
- Modify: `README.md` only if package architecture documentation is now inaccurate

**Interfaces:**
- Consumes: completed adapters and tests from Tasks 2–8.
- Produces: one coherent account-service package tree with no stale classes, empty folders, or old imports.

- [ ] **Step 1: Search for legacy imports and references before deletion.**

  Run:

  ```bash
  grep -RInE 'application\.dto|application\.mapper|application\.usecase|domain\.repository|infrastructure\.web|infrastructure\.persistence|infrastructure\.messaging' account-service/src || true
  ```

  Update remaining references to the new package names before deleting any file.

- [ ] **Step 2: Remove only obsolete source files and empty directories.**

  Confirm each old class is replaced and no test or generated source references it. Do not remove shared resources, SQL, configuration, or user changes.

- [ ] **Step 3: Add Javadocs to public ports, commands, results, services, adapters, and configuration methods.**

  Follow the existing `customer-service` documentation style: concise first sentence, `{@inheritDoc}` for interface implementations, and descriptions for public factory methods and ports.

- [ ] **Step 4: Run complete verification.**

  Run:

  ```bash
  ./mvnw -pl account-service test
  ./mvnw test
  ./mvnw -pl account-service -DskipTests compile
  find account-service/src/main/java -type d -empty -print
  git diff --check
  ```

  Expected: focused tests, full reactor tests, and compilation pass; the empty-directory scan returns no source directories; whitespace validation passes.

- [ ] **Step 5: Update the code graph.**

  Run:

  ```bash
  graphify update .
  ```

- [ ] **Step 6: Review the final diff and commit the cleanup.**

  ```bash
  git diff --stat
  git status --short
  git add account-service README.md
  git commit -m "refactor: complete account service hexagonal migration"
  ```

## Final verification checklist

- [ ] No Spring, JPA, validation, HTTP, or RabbitMQ imports remain in `application` or `domain`.
- [ ] Account, movement, and report input ports are explicit and controllers depend only on them.
- [ ] Output repository ports live under `application.port.out` and adapters implement them.
- [ ] Transaction annotations live only in infrastructure input adapters/configuration.
- [ ] HTTP routes and response contracts remain unchanged.
- [ ] Customer-event payload and RabbitMQ topology remain unchanged.
- [ ] H2 creates `account_service` before Hibernate starts.
- [ ] `./mvnw -pl account-service test` passes.
- [ ] `./mvnw test` passes.
- [ ] `graphify update .` completes.
