package com.devsu.customerservice.infrastructure.adapter.out.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.devsu.customerservice.application.port.out.ClienteEvent;
import com.devsu.customerservice.application.port.out.ClienteEventType;
import com.devsu.customerservice.domain.model.Cliente;
import com.devsu.customerservice.domain.model.DatosPersona;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ClienteEventCompatibilityTest {

  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  @Test
  void keepsCurrentCustomerEventFieldsAndValues() throws Exception {
    Cliente customer = new Cliente(
        "CLI-001",
        new DatosPersona("Jose Lema", "M", 35, "10001", "Otavalo", "098"),
        "1234",
        true);
    ClienteEvent event = ClienteEvent.from(ClienteEventType.CLIENTE_CREADO, customer);

    String json = objectMapper.writeValueAsString(event);

    assertThat(json).contains("\"eventType\":\"CLIENTE_CREADO\"");
    assertThat(json).contains("\"clienteId\":\"CLI-001\"");
    assertThat(json).contains("\"identificacion\":\"10001\"");
    assertThat(json).contains("\"estado\":true");
  }
}
