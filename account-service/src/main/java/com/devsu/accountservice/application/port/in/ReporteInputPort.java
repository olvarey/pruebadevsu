package com.devsu.accountservice.application.port.in;

import com.devsu.accountservice.application.result.EstadoCuentaResult;
import java.util.List;

/** Inbound port for account statement reports. */
public interface ReporteInputPort {

  /** Returns account statement rows for a customer and date range. */
  List<EstadoCuentaResult> getEstadoCuenta(String fecha, String clienteId);
}
