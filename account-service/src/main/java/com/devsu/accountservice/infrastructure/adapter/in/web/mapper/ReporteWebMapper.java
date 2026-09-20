package com.devsu.accountservice.infrastructure.adapter.in.web.mapper;

import com.devsu.accountservice.application.result.EstadoCuentaResult;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.EstadoCuentaResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/** Maps account statement application results to HTTP responses. */
@Component
public class ReporteWebMapper {

  /** Converts application report results to HTTP response records. */
  public List<EstadoCuentaResponse> toResponses(List<EstadoCuentaResult> results) {
    return results.stream()
        .map(result -> new EstadoCuentaResponse(result.fecha(), result.cliente(), result.numeroCuenta(),
            result.tipo(), result.saldoInicial(), result.estado(), result.movimiento(), result.saldoDisponible()))
        .toList();
  }
}
