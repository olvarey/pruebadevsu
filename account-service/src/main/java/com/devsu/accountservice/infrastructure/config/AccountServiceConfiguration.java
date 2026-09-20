package com.devsu.accountservice.infrastructure.config;

import com.devsu.accountservice.application.port.in.CuentaInputPort;
import com.devsu.accountservice.application.port.in.MovimientoInputPort;
import com.devsu.accountservice.application.port.in.ReporteInputPort;
import com.devsu.accountservice.application.port.out.CuentaRepository;
import com.devsu.accountservice.application.port.out.MovimientoRepository;
import com.devsu.accountservice.application.service.CuentaApplicationService;
import com.devsu.accountservice.application.service.MovimientoApplicationService;
import com.devsu.accountservice.application.service.ReporteApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/** Spring wiring for the account application core. */
@Configuration
public class AccountServiceConfiguration {

  /** Creates the framework-free account application service. */
  @Bean
  CuentaApplicationService cuentaApplicationService(CuentaRepository repository) {
    return new CuentaApplicationService(repository);
  }

  /** Creates the framework-free movement application service. */
  @Bean
  MovimientoApplicationService movimientoApplicationService(
      CuentaRepository cuentaRepository, MovimientoRepository movimientoRepository) {
    return new MovimientoApplicationService(cuentaRepository, movimientoRepository);
  }

  /** Creates the framework-free report application service. */
  @Bean
  ReporteApplicationService reporteApplicationService(
      CuentaRepository cuentaRepository, MovimientoRepository movimientoRepository) {
    return new ReporteApplicationService(cuentaRepository, movimientoRepository);
  }

  /** Exposes the transactional account input port. */
  @Bean
  @Primary
  CuentaInputPort cuentaInputPort(CuentaApplicationService service) {
    return new TransactionalCuentaInputAdapter(service);
  }

  /** Exposes the transactional movement input port. */
  @Bean
  @Primary
  MovimientoInputPort movimientoInputPort(MovimientoApplicationService service) {
    return new TransactionalMovimientoInputAdapter(service);
  }

  /** Exposes the transactional report input port. */
  @Bean
  @Primary
  ReporteInputPort reporteInputPort(ReporteApplicationService service) {
    return new TransactionalReporteInputAdapter(service);
  }
}
