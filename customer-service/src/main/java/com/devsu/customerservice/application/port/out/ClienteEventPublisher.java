package com.devsu.customerservice.application.port.out;

/** Outbound port for customer event publication. */
public interface ClienteEventPublisher {

  /** Publishes a customer event through an external messaging adapter. */
  void publish(ClienteEvent event);
}
