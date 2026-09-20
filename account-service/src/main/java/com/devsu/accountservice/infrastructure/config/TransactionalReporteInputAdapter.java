package com.devsu.accountservice.infrastructure.config;

import com.devsu.accountservice.application.port.in.ReporteInputPort;
import com.devsu.accountservice.application.result.EstadoCuentaResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/** Infrastructure transaction boundary for report use cases. */
@RequiredArgsConstructor
public class TransactionalReporteInputAdapter implements ReporteInputPort {

  private final ReporteInputPort delegate;

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<EstadoCuentaResult> getEstadoCuenta(String fecha, String clienteId) {
    return delegate.getEstadoCuenta(fecha, clienteId);
  }
}
