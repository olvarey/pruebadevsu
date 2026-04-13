package com.devsu.customerservice.infrastructure.messaging;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** RabbitMQ configuration for publishing customer events. */
@Configuration
@EnableConfigurationProperties(CustomerEventMessagingProperties.class)
public class CustomerEventMessagingConfig {

  /** Declares the durable exchange used for customer events. */
  @Bean
  DirectExchange customerEventsExchange(CustomerEventMessagingProperties properties) {
    return ExchangeBuilder.directExchange(properties.exchange()).durable(true).build();
  }

  /** Serializes AMQP messages as JSON. */
  @Bean
  Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
