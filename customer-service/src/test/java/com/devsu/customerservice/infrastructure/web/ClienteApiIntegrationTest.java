package com.devsu.customerservice.infrastructure.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devsu.customerservice.application.event.ClienteEvent;
import com.devsu.customerservice.application.event.ClienteEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Integration tests for the customer HTTP API. */
@AutoConfigureMockMvc
@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:customer_api_integration_test;"
          + "MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.rabbitmq.listener.simple.auto-startup=false"
    })
class ClienteApiIntegrationTest {

  private final MockMvc mockMvc;

  @Autowired
  ClienteApiIntegrationTest(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  void customerCrudFlowWorksThroughHttpApi() throws Exception {
    createCustomer();

    mockMvc
        .perform(get("/api/clientes/CLI-E2E-001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.clienteId").value("CLI-E2E-001"))
        .andExpect(jsonPath("$.data.nombre").value("Jose Lema"));

    mockMvc
        .perform(
            patch("/api/clientes/CLI-E2E-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "direccion": "Amazonas y NNUU",
                      "telefono": "097548965"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.direccion").value("Amazonas y NNUU"))
        .andExpect(jsonPath("$.data.telefono").value("097548965"));
  }

  private void createCustomer() throws Exception {
    mockMvc
        .perform(
            post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "clienteId": "CLI-E2E-001",
                      "nombre": "Jose Lema",
                      "genero": "Masculino",
                      "edad": 32,
                      "identificacion": "0102030405",
                      "direccion": "Otavalo sn y principal",
                      "telefono": "0987654321",
                      "contrasena": "1234",
                      "estado": true
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.clienteId").value("CLI-E2E-001"));
  }

  /** Replaces RabbitMQ publishing with an in-memory no-op for API integration tests. */
  @TestConfiguration
  static class MessagingTestConfig {

    /** Provides a no-op publisher so tests do not require a RabbitMQ broker. */
    @Bean
    @Primary
    ClienteEventPublisher clienteEventPublisher() {
      return new NoOpClienteEventPublisher();
    }
  }

  private static class NoOpClienteEventPublisher implements ClienteEventPublisher {

    @Override
    public void publish(ClienteEvent event) {
      // No external messaging in integration tests.
    }
  }
}
