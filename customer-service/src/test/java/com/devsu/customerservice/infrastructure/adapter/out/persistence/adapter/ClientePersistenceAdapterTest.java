package com.devsu.customerservice.infrastructure.adapter.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.devsu.customerservice.domain.model.Cliente;
import com.devsu.customerservice.domain.model.DatosPersona;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.boot.test.context.SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:customer_adapter_test;MODE=PostgreSQL;"
          + "DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS customer_service",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.rabbitmq.listener.simple.auto-startup=false"
    })
class ClientePersistenceAdapterTest {

  @Autowired
  private ClientePersistenceAdapter adapter;

  @Test
  void persistsAndFindsCustomerThroughOutboundPort() {
    Cliente customer = new Cliente(
        "CLI-001",
        new DatosPersona("Jose Lema", "M", 35, "10001", "Otavalo", "098"),
        "1234",
        true);

    adapter.save(customer);

    assertThat(adapter.findByClienteId("CLI-001")).isPresent()
        .get().extracting(Cliente::getIdentificacion).isEqualTo("10001");
    assertThat(adapter.existsByClienteId("CLI-001")).isTrue();
  }
}
