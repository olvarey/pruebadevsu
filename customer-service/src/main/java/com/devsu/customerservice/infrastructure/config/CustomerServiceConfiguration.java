package com.devsu.customerservice.infrastructure.config;

import com.devsu.customerservice.application.port.in.ClienteInputPort;
import com.devsu.customerservice.application.port.out.ClienteEventPublisher;
import com.devsu.customerservice.application.port.out.ClienteRepository;
import com.devsu.customerservice.application.service.CustomerApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/** Spring wiring for the application core. */
@Configuration
public class CustomerServiceConfiguration {

  /** Creates the framework-free customer application service. */
  @Bean
  CustomerApplicationService customerApplicationService(
      ClienteRepository clienteRepository, ClienteEventPublisher clienteEventPublisher) {
    return new CustomerApplicationService(clienteRepository, clienteEventPublisher);
  }

  /** Exposes the transactional inbound port used by web adapters. */
  @Primary
  @Bean
  ClienteInputPort clienteInputPort(CustomerApplicationService applicationService) {
    return new TransactionalClienteInputAdapter(applicationService);
  }
}
