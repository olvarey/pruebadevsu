package com.devsu.accountservice.infrastructure.adapter.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.model.DatosCuenta;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Integration tests for the account persistence adapter. */
@SpringBootTest(properties = {
  "spring.datasource.url=jdbc:h2:mem:account_persistence_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS account_service",
  "spring.datasource.driver-class-name=org.h2.Driver",
  "spring.jpa.hibernate.ddl-auto=create-drop",
  "spring.rabbitmq.listener.simple.auto-startup=false"
})
class CuentaPersistenceAdapterTest {

  private final CuentaPersistenceAdapter adapter;

  @Autowired
  CuentaPersistenceAdapterTest(CuentaPersistenceAdapter adapter) {
    this.adapter = adapter;
  }

  @Test
  void savesAndFindsAccountDomainObjects() {
    Cuenta saved = adapter.save(new Cuenta("PERSIST-001",
        new DatosCuenta("Ahorro", new BigDecimal("100.00"), new BigDecimal("100.00"), true, "CLI-001")));

    assertThat(adapter.findByClienteId("CLI-001")).extracting(Cuenta::getNumeroCuenta)
        .contains("PERSIST-001");
    assertThat(adapter.findByNumeroCuenta(saved.getNumeroCuenta())).isPresent();
  }
}
