package com.devsu.accountservice.infrastructure.adapter.in.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/** Verifies compatibility with customer-service event JSON. */
class ClienteEventCompatibilityTest {

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Test
  void deserializesThePublishedCustomerEventPayload() throws Exception {
    ClienteEventMessage event = objectMapper.readValue(
        """
        {
          "eventId": "event-001",
          "eventType": "CUSTOMER_UPDATED",
          "occurredAt": "2026-04-10T09:00:00Z",
          "clienteId": "CLI-001",
          "nombre": "Jose Lema",
          "identificacion": "0102030405",
          "estado": true
        }
        """,
        ClienteEventMessage.class);

    assertThat(event.eventId()).isEqualTo("event-001");
    assertThat(event.eventType()).isEqualTo("CUSTOMER_UPDATED");
    assertThat(event.occurredAt()).isEqualTo(Instant.parse("2026-04-10T09:00:00Z"));
    assertThat(event.clienteId()).isEqualTo("CLI-001");
    assertThat(event.nombre()).isEqualTo("Jose Lema");
    assertThat(event.identificacion()).isEqualTo("0102030405");
    assertThat(event.estado()).isTrue();
  }
}
