package com.devsu.accountservice.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Receives customer events published by the customer service. */
@Component
public class ClienteEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClienteEventListener.class);

  /** Handles customer events received from RabbitMQ. */
  @RabbitListener(queues = "${app.messaging.customer-events.queue}")
  public void handle(ClienteEventMessage event) {
    LOGGER.info(
        "Received customer event eventId={} eventType={} clienteId={}",
        event.eventId(),
        event.eventType(),
        event.clienteId());
  }
}
