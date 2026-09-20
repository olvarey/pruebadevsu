package com.devsu.accountservice.infrastructure.adapter.in.messaging;

import java.time.Instant;

/** Customer event payload consumed by the account service. */
public record ClienteEventMessage(
    String eventId,
    String eventType,
    Instant occurredAt,
    String clienteId,
    String nombre,
    String identificacion,
    boolean estado) {}
