package com.devsu.customerservice.infrastructure.adapter.out.messaging;

import com.devsu.customerservice.application.port.out.ClienteEvent;
import com.devsu.customerservice.application.port.out.ClienteEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** RabbitMQ adapter for customer event publishing. */
@Component
@RequiredArgsConstructor
public class RabbitClienteEventPublisher implements ClienteEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final CustomerEventMessagingProperties properties;

  /** {@inheritDoc} */
  @Override
  public void publish(ClienteEvent event) {
    rabbitTemplate.convertAndSend(properties.exchange(), properties.routingKey(), event);
  }
}
