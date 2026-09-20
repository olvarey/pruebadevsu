package com.devsu.accountservice.infrastructure.adapter.in.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** RabbitMQ settings for customer events consumed by the account service. */
@ConfigurationProperties(prefix = "app.messaging.customer-events")
public record CustomerEventMessagingProperties(
    String exchange, String queue, String routingKey) {}
