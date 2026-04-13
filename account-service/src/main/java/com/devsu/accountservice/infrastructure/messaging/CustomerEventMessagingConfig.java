package com.devsu.accountservice.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** RabbitMQ configuration for consuming customer events. */
@Configuration
@EnableConfigurationProperties(CustomerEventMessagingProperties.class)
public class CustomerEventMessagingConfig {

  /** Declares the durable customer events exchange. */
  @Bean
  DirectExchange customerEventsExchange(CustomerEventMessagingProperties properties) {
    return ExchangeBuilder.directExchange(properties.exchange()).durable(true).build();
  }

  /** Declares the account service queue for customer events. */
  @Bean
  Queue accountCustomerEventsQueue(CustomerEventMessagingProperties properties) {
    return QueueBuilder.durable(properties.queue()).build();
  }

  /** Binds the account service queue to customer events. */
  @Bean
  Binding accountCustomerEventsBinding(
      Queue accountCustomerEventsQueue,
      DirectExchange customerEventsExchange,
      CustomerEventMessagingProperties properties) {
    return BindingBuilder.bind(accountCustomerEventsQueue)
        .to(customerEventsExchange)
        .with(properties.routingKey());
  }

  /** Deserializes AMQP messages from JSON. */
  @Bean
  Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
