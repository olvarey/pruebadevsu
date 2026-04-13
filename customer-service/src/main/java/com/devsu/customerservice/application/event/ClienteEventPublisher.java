package com.devsu.customerservice.application.event;

/** Publishes customer events to external consumers. */
public interface ClienteEventPublisher {

  /** Publishes a customer event. */
  void publish(ClienteEvent event);
}
