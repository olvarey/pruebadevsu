package com.devsu.accountservice.infrastructure.adapter.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.devsu.accountservice.domain.model.DatosMovimiento;
import com.devsu.accountservice.domain.model.Movimiento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Integration tests for the movement persistence adapter. */
@SpringBootTest(properties = {
  "spring.datasource.url=jdbc:h2:mem:movement_persistence_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS account_service",
  "spring.datasource.driver-class-name=org.h2.Driver",
  "spring.jpa.hibernate.ddl-auto=create-drop",
  "spring.rabbitmq.listener.simple.auto-startup=false"
})
class MovimientoPersistenceAdapterTest {

  private final MovimientoPersistenceAdapter adapter;

  @Autowired
  MovimientoPersistenceAdapterTest(MovimientoPersistenceAdapter adapter) {
    this.adapter = adapter;
  }

  @Test
  void savesAndFindsMovementsInsideDateRange() {
    LocalDateTime date = LocalDateTime.parse("2026-04-10T09:00:00");
    Movimiento saved = adapter.save(new Movimiento("PERSIST-MOV-001",
        new DatosMovimiento("478758", date, "Deposito", new BigDecimal("10.00"), new BigDecimal("110.00"))));

    assertThat(adapter.findByMovimientoId(saved.getMovimientoId())).isPresent();
    assertThat(adapter.findByNumeroCuentaInAndFechaBetween(
        java.util.List.of("478758"), date.minusDays(1), date.plusDays(1)))
        .extracting(Movimiento::getMovimientoId).containsExactly("PERSIST-MOV-001");
  }
}
