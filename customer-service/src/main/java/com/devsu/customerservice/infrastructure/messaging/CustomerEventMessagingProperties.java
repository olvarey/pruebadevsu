package com.devsu.customerservice.infrastructure.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** RabbitMQ settings for customer events. */
@ConfigurationProperties(prefix = "app.messaging.customer-events")
public record CustomerEventMessagingProperties(String exchange, String routingKey) {}
