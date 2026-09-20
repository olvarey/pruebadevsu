package com.devsu.customerservice.infrastructure.adapter.out.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** RabbitMQ settings for customer events. */
@ConfigurationProperties(prefix = "app.messaging.customer-events")
/** Configuration values used by the customer event messaging adapter. */
public record CustomerEventMessagingProperties(String exchange, String routingKey) {}
