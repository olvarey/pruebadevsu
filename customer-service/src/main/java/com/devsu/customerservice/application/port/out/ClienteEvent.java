package com.devsu.customerservice.application.port.out;

import com.devsu.customerservice.domain.model.Cliente;
import java.time.Instant;
import java.util.UUID;

/** Application event contract sent to an outbound messaging adapter. */
public record ClienteEvent(
    String eventId,
    ClienteEventType eventType,
    Instant occurredAt,
    String clienteId,
    String nombre,
    String identificacion,
    boolean estado) {

  /** Creates an event snapshot from a customer aggregate. */
  public static ClienteEvent from(ClienteEventType eventType, Cliente cliente) {
    return new ClienteEvent(
        UUID.randomUUID().toString(),
        eventType,
        Instant.now(),
        cliente.getClienteId(),
        cliente.getNombre(),
        cliente.getIdentificacion(),
        cliente.isEstado());
  }
}
