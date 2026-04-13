package com.devsu.customerservice.application.event;

import com.devsu.customerservice.domain.model.Cliente;
import java.time.Instant;
import java.util.UUID;

/** Event published when customer data changes. */
public record ClienteEvent(
    String eventId,
    ClienteEventType eventType,
    Instant occurredAt,
    String clienteId,
    String nombre,
    String identificacion,
    boolean estado) {

  /** Creates an event from the current customer state. */
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
